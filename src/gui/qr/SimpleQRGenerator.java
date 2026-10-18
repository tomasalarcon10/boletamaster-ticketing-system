package gui.qr;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class SimpleQRGenerator {
    
	public static BufferedImage generateQRImage(String data, int size) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    size,
                    size
            );

            return MatrixToImageWriter.toBufferedImage(matrix);

        } catch (WriterException e) {
            System.err.println("Error generando QR: " + e.getMessage());
            return null;
        }
    }

    public static String generateQRData(String evento, String idTiquete, String fechaEvento, String fechaImpresion) {
        return String.format(
            "BOLETAMASTER|EV:%s|ID:%s|FECHA_EVT:%s|FECHA_IMP:%s|VALIDO",
            evento, idTiquete, fechaEvento, fechaImpresion
        );
    }
}