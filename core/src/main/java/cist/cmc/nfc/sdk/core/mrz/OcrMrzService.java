package cist.cmc.nfc.sdk.core.mrz;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

@Keep
public class OcrMrzService {
    public TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    @Keep
    public void progressFromFilePath(
            Context context,
            Uri uri,
            OcrResultCallBack callBack
    ) throws IOException {
        InputImage image = InputImage.fromFilePath(context, uri);
        progressImage(image, callBack);
    }

    @Keep
    public void progressFromByteBuffer(
            ByteBuffer byteBuffer,
            int width,
            int height,
            int rotationDegrees,
            int format,
            OcrResultCallBack callBack
    ) {
        InputImage image = InputImage.fromByteBuffer(byteBuffer, width, height, rotationDegrees, format);
        progressImage(image, callBack);
    }

    @Keep
    public void progressFromByteArray(byte[] bytes,
                                      int width,
                                      int height,
                                      int rotationDegrees,
                                      int format,
                                      OcrResultCallBack callBack
    ) {
        InputImage image = InputImage.fromByteArray(bytes, width, height, rotationDegrees, format);
        progressImage(image, callBack);
    }

    @Keep
    public void progressFromMediaImage(Image mediaImage, int rotationDegrees, OcrResultCallBack callBack) {
        InputImage image = InputImage.fromMediaImage(mediaImage, rotationDegrees);
        progressImage(image, callBack);
    }

    public void progressFromBitmap(Bitmap bitmap, int rotationDegrees, OcrResultCallBack callBack) {
        InputImage image = InputImage.fromBitmap(bitmap, rotationDegrees);
        progressImage(image, callBack);
    }


    private void progressImage(InputImage image, OcrResultCallBack callBack) {
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        MrzInfo info = progressTextRecognitionResult(text);
                        if (info != null) {
                            callBack.onSuccess(info);
                        } else {
                            callBack.onFailure(new Exception("No MrzInfo"));
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callBack.onFailure(e);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Text>() {
                    @Override
                    public void onComplete(@NonNull Task<Text> task) {
                        callBack.onComplete();
                    }
                });
    }

    private MrzInfo progressTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            return null;
        }
        StringBuilder textBuilder = new StringBuilder();

        for (int i = 0; i < blocks.size(); i++) {
            String text = blocks.get(i).getText()
                    .replace(" ", "")
                    .replace("\n", "");
            textBuilder.append(text);
        }

        String[] textList = textBuilder.toString().split("IDVNM");
        if (textList.length >= 2) {
            String text = textList[1];
            text = "IDVNM" + text;
            if (text.length() >= 60) {
                String documentNumberFullFixed = MRZFieldRecognitionDefectsFixer.fixDocumentNumber(text.substring(15, 27));
                String documentNumberFixed
                        = MRZFieldRecognitionDefectsFixer.fixDocumentNumber(text.substring(5, 14));
                String documentNumberCheckDigitFixed = MRZFieldRecognitionDefectsFixer.fixCheckDigit(text.substring(14, 15));
                String birthDateFixed = MRZFieldRecognitionDefectsFixer.fixDate(text.substring(30, 36));
                String birthDateCheckDigitFixed = MRZFieldRecognitionDefectsFixer.fixCheckDigit(text.substring(36, 37));
                String expiryDateFixed = MRZFieldRecognitionDefectsFixer.fixDate(text.substring(38, 44));
                String expiryDateCheckDigitFixed = MRZFieldRecognitionDefectsFixer.fixCheckDigit(text.substring(44, 45));
                if (!documentNumberCheckDigitFixed
                        .equals(String.valueOf(MRZCheckDigitCalculator.GetCheckDigit(documentNumberFixed)))) {
                    return null;
                }
                if (!birthDateCheckDigitFixed
                        .equals(String.valueOf(MRZCheckDigitCalculator.GetCheckDigit(birthDateFixed)))) {
                    return null;
                }
                if (!expiryDateCheckDigitFixed
                        .equals(String.valueOf(MRZCheckDigitCalculator.GetCheckDigit(expiryDateFixed)))) {
                    return null;
                }
                return new MrzInfo(documentNumberFixed, birthDateFixed, expiryDateFixed, documentNumberFullFixed);
            }
        }

        return null;
    }
}
