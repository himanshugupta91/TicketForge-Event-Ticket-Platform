package com.himanshu.tickets.services.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.himanshu.tickets.domain.entities.QrCode;
import com.himanshu.tickets.domain.entities.QrCodeStatusEnum;
import com.himanshu.tickets.domain.entities.Ticket;
import com.himanshu.tickets.exceptions.QrCodeGenerationException;
import com.himanshu.tickets.exceptions.QrcodeNotFoundException;
import com.himanshu.tickets.repositories.QrCodeRepository;
import com.himanshu.tickets.services.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class QrCodeServiceImpl implements QrCodeService {


    private static final int QRCODE_WIDTH = 300;
    private static final int QRCODE_HEIGHT = 300;

    private final QRCodeWriter  qrCodeWriter;
    private final QrCodeRepository qrCodeRepository;


    @Override
    public QrCode generateQrCode(Ticket ticket) {
       try {
           UUID uniqueId = UUID.randomUUID();
           String qrCodeImage = generateQrCodeImage(uniqueId);

           QrCode qrCode = new QrCode();
           qrCode.setId(uniqueId);
           qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
           qrCode.setValue(qrCodeImage);
           qrCode.setTicket(ticket);

           return qrCodeRepository.saveAndFlush(qrCode);

       }catch (WriterException | IOException ex){
           throw new QrCodeGenerationException("Failed to generate QR code", ex);
       }
    }

    @Override
    public byte[] getQrCodeImageForUserAndTicket(UUID userId, UUID ticketId) {
        QrCode qrCode = qrCodeRepository.findByTicketIdAndTicketPurchaserId(ticketId, userId)
                .orElseThrow((QrcodeNotFoundException::new));
        try {
            return Base64.getDecoder().decode(qrCode.getValue());
        }catch (IllegalArgumentException ex){
            log.error("Invalid base64 QR Code For ticket ID : {}", ticketId, ex);
            throw new QrcodeNotFoundException();
        }
    }

    private String generateQrCodeImage(UUID uniqueId) throws WriterException, IOException {
        BitMatrix bitMatrix = qrCodeWriter.encode(
                uniqueId.toString(),
                BarcodeFormat.QR_CODE,
                QRCODE_WIDTH,
                QRCODE_HEIGHT);

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrCodeImage, "PNG", byteArrayOutputStream);

            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
