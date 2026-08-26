package com.dongah.fastcharger.TECH3800;

import android.annotation.SuppressLint;
import android.util.Log;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.TECH3800.packet.PacketHeader;
import com.dongah.fastcharger.TECH3800.packet.PacketPay;
import com.dongah.fastcharger.TECH3800.packet.PacketPayCancel;
import com.dongah.fastcharger.TECH3800.packet.PacketPayG;
import com.dongah.fastcharger.basefunction.ChargerConfiguration;
import com.dongah.fastcharger.basefunction.ChargingCurrentData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;

import android_serialport_api.SerialPort;

public class TLS3800 extends TLS3800Reader implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(TLS3800.class);


    @SuppressLint("SimpleDateFormat")
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final byte SOH = (byte) 0x01;
    public static final byte STX = (byte) 0x02;
    public static final byte ETX = (byte) 0x03;
    public static final byte EOT = (byte) 0x04;
    public static final byte ENQ = (byte) 0x05;
    public static final byte ACK = (byte) 0x06; //정상
    public static final byte SYN = (byte) 0x16;
    public static final byte CR = (byte) 0x0d;
    public static final byte LF = (byte) 0x0a;
    public static final byte NACK = (byte) 0x15; //오류


    /**
     * command TX ( 연동 장치 ==> 결제기 )
     */
    public static final byte CMD_TX_TREMCHACK = (byte) 'A';
    public static final byte CMD_TX_PAY = (byte) 'B';
    public static final byte CMD_TX_PAYCANCEL = (byte) 'C';
    public static final byte CMD_TX_RF_READ = (byte) 'D';
    public static final byte CMD_TX_PAY_G = (byte) 'G';
    public static final byte CMD_TX_RETURN = (byte) 'E';
    public static final byte CMD_TX_UID = (byte) 'F';
    public static final byte CMD_TX_IC_CHECK = (byte) 'M';
    public static final byte CMD_TX_RESET = (byte) 'R';
    public static final byte CMD_TX_SITE_INFO = (byte) '4';

    /**
     * command RX ( 결제기 ==> 연동 장치 )
     */
    public static final byte CMD_RX_TREMCHACK = (byte) 'a';
    public static final byte CMD_RX_PAY = (byte) 'b';
    public static final byte CMD_RX_PAYCANCEL = (byte) 'c';
    public static final byte CMD_RX_RF_READ = (byte) 'd';
    public static final byte CMD_RX_PAY_G = (byte) 'g';
    public static final byte CMD_RX_RETURN = (byte) 'e';
    public static final byte CMD_RX_UID = (byte) 'f';
    public static final byte CMD_RX_IC_CHECK = (byte) 'm';
    public static final byte CMD_RX_RESET = (byte) 'r';
    public static final byte CMD_RX_EVENT = (byte) '@';
    public static final byte CMD_RX_SITE_INFO = (byte) '4';


    private SerialPort serialPort;
    private String deviceComportName;
    private InputStream inputStream;
    private OutputStream outputStream;

    ConvertDataType convertDataType;
    TLS3800State state = TLS3800State.None;
    Thread receiveThread;

    int ch;
    byte jobCode;
    int cancelType = 0;
    String cardNumber;
    ChargerConfiguration chargerConfiguration;
    ChargingCurrentData chargingCurrentData;
    HashMap<String, String> result;
    byte[] byteResult;
    // 결제 정보
    String tradeCode, tradeMethod, creditCardNumber, approvalAmount, surTax;
    String tip, installment, approvalNumber, approvalDate, approvalTime;
    String tradeUniqueNumber, storeNumber, terminalNumber, issuer, buyer;
    String dash, responseCode, responseMessage, pgTranSeq;

    public TLS3800(String deviceComportName) {
        this.deviceComportName = deviceComportName;
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        chargerConfiguration = ((MainActivity) MainActivity.mContext).getChargerConfiguration();
        try {
            convertDataType = new ConvertDataType();
            serialPort = new SerialPort(new File(deviceComportName), 115200, 0);


            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
            receiveThread = new Thread(this);
            receiveThread.start();
            SetState(TLS3800State.Ready);
            //Hash map
            result = new HashMap<>();
        } catch (Exception e) {
            logger.error(" TLS3800 initial error : {} ", e.getMessage());
        }
    }

    /**
     * TL3800 request
     *
     * @param ch         : current channel
     * @param cmd        : command
     * @param cancelType : 0: not / 4: 무카드 취소 / 5: PG 부분 취소
     */
    @Override
    public void onTLS3800Request(int ch, byte cmd, int cancelType) {
        try {
            setCh(ch);
            setCancelType(cancelType);
            switch (cmd) {
                case CMD_TX_SITE_INFO:
                case CMD_TX_TREMCHACK:
                case CMD_TX_RF_READ:
                case CMD_TX_IC_CHECK:
                case CMD_TX_RETURN:
                case CMD_TX_RESET:
                    PacketHeader packetHeader = new PacketHeader();
                    packetHeader.setSTX((byte) 0x02);
                    packetHeader.setTerminalID(convertDataType.padRightChar(chargerConfiguration.getMID(), 16, (char) 0x00));
                    packetHeader.setDateTime((sdf.format(new Date())).toCharArray());
                    packetHeader.setJobCode((char) cmd);
                    packetHeader.setDataLength((short) 0);
                    outputStream.write(packetHeader.onHeaderDataSet());
                    break;
                case CMD_TX_PAY:
//                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getClassUiProcess(getCh()).getChargingCurrentData();
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getCh());
                    PacketPay packetPay = new PacketPay();
                    packetPay.setTerminalID(convertDataType.padRightChar(chargerConfiguration.getMID(), 16, (char) 0x00));
                    packetPay.setDateTime((sdf.format(new Date())).toCharArray());
                    packetPay.setJobCode((char) cmd);
                    packetPay.setDataLength((short) 30);
                    packetPay.setTradeCode((char) 0x31);
                    //padLeftChar
                    packetPay.setPrePayment(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getPrePayment()), 10, "0").toCharArray());
                    packetPay.setSurTax(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getSurtax()), 8, "0").toCharArray());
                    packetPay.setTip(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getTip()), 8, "0").toCharArray());
                    packetPay.setInstallment(convertDataType.padLeftChar("00", 2, "0").toCharArray());
                    packetPay.setSign((char) 0x31);
                    outputStream.write(packetPay.onPayDataSet());
                    break;
                case CMD_TX_PAYCANCEL:
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getCh());
                    PacketPayCancel packetPayCancel = new PacketPayCancel(cancelType);
                    packetPayCancel.setSTX(STX);
                    packetPayCancel.setTerminalID(convertDataType.padRightChar(chargerConfiguration.getMID(), 16, (char) 0x00));
                    packetPayCancel.setDateTime((sdf.format(new Date())).toCharArray());
                    packetPayCancel.setJobCode((char) cmd);
                    switch (getCancelType()) {
                        case 4:
                            //무카드 취소
                            packetPayCancel.setCancelCode(PacketPayCancel.PAY_CANCEL);
                            packetPayCancel.setApprovalAmount(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getPrePayment()), 10, "0").toCharArray());
                            break;
                        case 5:
                            //PG 부분 취소
                            packetPayCancel.setCancelCode(PacketPayCancel.PAY_CANCEL_G);
                            packetPayCancel.setApprovalAmount(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getPartialCancelPayment()), 10, "0").toCharArray());
                            break;
                    }
                    packetPayCancel.setDataLength((short) 89);
                    //padLeftChar
                    packetPayCancel.setTradeCode((char) 0x32);
                    packetPayCancel.setSurTax(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getSurtax()), 8, "0").toCharArray());
                    packetPayCancel.setTip(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getTip()), 8, "0").toCharArray());
                    packetPayCancel.setInstallment(convertDataType.padLeftChar("00", 2, "0").toCharArray());
                    packetPayCancel.setSign((char) 0x31);
                    packetPayCancel.setApprovalNumber(convertDataType.padRightChar(chargingCurrentData.getApprovalNumber(), 12, (char) 0x20));
                    packetPayCancel.setTradeDate(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getApprovalDate()), 8, "0").toCharArray());
                    packetPayCancel.setTradeTime(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getApprovalTime()), 6, "0").toCharArray());

                    packetPayCancel.setAddInfoLength(convertDataType.padLeftChar("30", 2, "0").toCharArray());
                    packetPayCancel.setAddInfo(chargingCurrentData.getPgTranSeq().toCharArray());

                    outputStream.write(packetPayCancel.onPayCancelDataSet());
                    break;
                case CMD_TX_PAY_G:
                    chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getCh());
                    PacketPayG packetPayG = new PacketPayG();
                    packetPayG.setSTX((byte) 0x02);
                    packetPayG.setTerminalID(convertDataType.padRightChar(chargerConfiguration.getMID(), 16, (char) 0x00));
                    packetPayG.setDateTime((sdf.format(new Date())).toCharArray());
                    packetPayG.setJobCode((char) cmd);
                    packetPayG.setDataLength((short) 339);
                    //padLeftChar
                    packetPayG.setTradeCode((char) 0x31);
                    packetPayG.setApprovalAmount(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getPrePayment()), 10, "0").toCharArray());
                    packetPayG.setSurTax(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getSurtax()), 8, "0").toCharArray());
                    packetPayG.setTip(convertDataType.padLeftChar(String.valueOf(chargingCurrentData.getTip()), 8, "0").toCharArray());
                    packetPayG.setInstallment(convertDataType.padLeftChar("00", 2, "0").toCharArray());
                    outputStream.write(packetPayG.onPayGDataSet());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("onTLS3800Request error : {}", e.getMessage());
        }
    }

    private static final int HEADER_SIZE = 35;
    private static final int RECV_BUF_SIZE = 1024;
    int recvPos = 0;

    @Override
    public void run() {
        int readCnt = 0, readRemainCnt = 0, subCount = 0;
        byte[] readData = new byte[512];
        byte[] buffer = new byte[512];
        byte chkJobCode = 0x00;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Arrays.fill(readData, (byte) 0x00);
                Arrays.fill(buffer, (byte) 0x00);
                readCnt = inputStream.available();
                if (readCnt > 1) {
                    subCount = readCnt;
                    int rCount = inputStream.read(readData, 0, readCnt);
                    Log.d("DONGAH", String.valueOf(readCnt));
                    Log.d("DONGAH", convertDataType.byteArrayToString(readData));

                    if (readData[0] == ACK) {
                        subCount--;
                        onAckResponse();
                        System.arraycopy(readData, 1, buffer, 0, readCnt - 1);
                    } else {
                        System.arraycopy(readData, 0, buffer, 0, readCnt);
                    }
                    chkJobCode = readData[readCnt - 1];
                    Log.d("DONGAH", convertDataType.byteArrayToString(buffer));

                    while (!checkData(buffer, subCount)) {
                        readRemainCnt = inputStream.available();
                        if (readRemainCnt == 0) break;
                        // job code 확인
                        Arrays.fill(readData, (byte) 0x00);
                        rCount = inputStream.read(readData, 0, readRemainCnt);
                        Log.d("DONGAH", String.valueOf(readRemainCnt));
                        Log.d("DONGAH", convertDataType.byteArrayToString(readData));


                        if ((readData[0] == 0x40 && readData[5] == 0x03) || chkJobCode == 0x40) {
                            Arrays.fill(buffer, (byte) 0x00);
                            System.arraycopy(readData, 6, buffer, 0, readRemainCnt - 6);
                            Log.d("DONGAH", "++" + convertDataType.byteArrayToString(buffer));
                            subCount = readRemainCnt - 6;
                        } else {
                            System.arraycopy(readData, 0, buffer, subCount, readRemainCnt);
                            subCount += readRemainCnt;
                        }

                    }
                    Log.d("DONGAH", String.valueOf(subCount));
                    Log.d("DONGAH", convertDataType.byteArrayToString(buffer));
                }


                if (Objects.equals(buffer[0], STX) && (subCount) > HEADER_SIZE) {
                    char job = (char) buffer[31];
                    jobCode = (byte) job;
                    byte dataLenLow = buffer[HEADER_SIZE - 2];
                    byte dataLenHigh = buffer[HEADER_SIZE - 1];
                    int dataLength = convertDataType.ByteArrayToShort(dataLenHigh, dataLenLow);
                    byteResult = new byte[dataLength];
                    result.clear();

                    //job code (소문자)
                    switch (jobCode) {
                        case 0x40:
                            break;
                        case CMD_RX_TREMCHACK:
                            // MID 설정 ( TerminalId = MID  header 사용)buffer
                            result.put("MID", new String(copyOf(buffer, 1, 16)).trim());
                            //parsing
                            System.arraycopy(buffer, 35, byteResult, 0, dataLength);
                            //카드 모듈 통신 상태 ( N[미설치]  O[정상] X[오류])
                            result.put("commStatus", String.valueOf(buffer[0]));
                            //RF 모듈 상태  “O”[정상], “X”[오류], “F”[장치 카드 처리자 문의(암호키 오류)]
                            result.put("rfStatus", String.valueOf(byteResult[1]));
                            //VAN 서버 연결 상태 (“N”[미설치], “O”[정상], “X”[연결 디바이스 오류] “F”[서버 연결 실패]
                            result.put("vanStatus", String.valueOf(byteResult[2]));
                            //연동 서버 연결 상태 “N”[미설치], “O”[정상], “X”[연결 디바이스 오류] “F”[서버 연결 실패]
                            result.put("serverStatus", String.valueOf(byteResult[3]));
                            if (tls3800Listener != null)
                                tls3800Listener.onTLS3800ResponseCallBack(getCh(), TLS3800ResponseType.CHECK, result);
                            //"a" 장치 체크 요청 전문 응답
                            onAckResponse();
                            break;
                        case CMD_RX_RETURN:
                            onAckResponse();
                            break;
                        case CMD_RX_PAY:
                        case CMD_RX_PAYCANCEL:
                            //cancel type 4:무카드 취소  5:부분 취소
                            result.put("cancelType", String.valueOf(getCancelType()));

                            //data parsing
                            System.arraycopy(buffer, 35, byteResult, 0, dataLength);
                            // 거래 구분 코드
                            // “1”[신용 승인], “2”[현금 영수증], “3”[선불 카드], “4”[제로 페이], “5”[카카오 페이(머니)] ,
                            // “6”[카카오 페이(신용)], “8”[네이버 페이] , “X”[거래 거절]:거래 매체~단말기 번호 space 채움,
                            // 취소 응답 시는 취소 요청 시 거래 구분 코드 전
                            tradeCode = new String(copyOf(byteResult, 0, 1));
                            result.put("tradeCode", tradeCode);
                            // 거래 매체
                            // “1”[IC], “2”[MS], “3”[RF], “4”[바코드], “5”[KEY IN],  MS/RF 무카드 취소에 대한 응답은 “2”로 응답
                            tradeMethod = new String(copyOf(byteResult, 1, 1));
                            result.put("tradeMethod", tradeMethod);
                            // 신용 카드 번호
                            creditCardNumber = new String(copyOf(byteResult, 2, 20));
                            result.put("creditCardNumber", creditCardNumber);
                            // 승인 금액
                            approvalAmount = new String(copyOf(byteResult, 22, 10));
                            result.put("approvalAmount", approvalAmount);
                            // 세금/잔여 횟수
                            surTax = new String(copyOf(byteResult, 32, 8));
                            result.put("surTax", surTax);
                            // 봉사료/사용 횟수
                            tip = new String(copyOf(byteResult, 40, 8));
                            result.put("tip", tip);
                            // 할부 개월
                            installment = new String(copyOf(byteResult, 48, 2));
                            result.put("installment", installment);
                            // 승인 번호/ 선불 카드 정보
                            approvalNumber = new String(copyOf(byteResult, 50, 12));
                            result.put("approvalNumber", approvalNumber);
                            // 매출 일자
                            approvalDate = new String(copyOf(byteResult, 62, 8));
                            result.put("approvalDate", approvalDate);
                            // 매출 시간
                            approvalTime = new String(copyOf(byteResult, 70, 6));
                            result.put("approvalTime", approvalTime);
                            // 거래 고유 번호
                            // 승인 시 수신한 거래 고유 번호[거래 날짜(6)+거래 일련 번호(6)], 선불 카드 경우 카드 거래
                            tradeUniqueNumber = new String(copyOf(byteResult, 76, 12));
                            result.put("tradeUniqueNumber", tradeUniqueNumber);
                            // 가맹점 번호
                            storeNumber = new String(copyOf(byteResult, 88, 15));
                            // 단말기 번호 ( 단말기 고유 번호 [단말기 ID(10)+거래 일련 번호(4)]  선불 카드의 경우 space[0x20] 채움
                            terminalNumber = new String(copyOf(byteResult, 103, 14));
                            if (Objects.equals(tradeCode, "X")) {
                                dash = new String(copyOf(byteResult, 117, 1));
                                responseCode = new String(copyOf(byteResult, 118, 2));
                                result.put("responseCode", responseCode);
                                responseMessage = new String(copyOf(byteResult, 120, 37));
                                result.put("responseMessage", responseMessage);
                            } else {
                                //
                                result.put("responseCode", "00");
                                result.put("responseMessage", "Success");
                                // 발급사
                                issuer = new String(copyOf(byteResult, 117, 20));
                                result.put("issuer", issuer);
                                // 매입사
                                buyer = new String(copyOf(byteResult, 137, 20));
                                result.put("buyer", buyer);
                            }

                            // "b" "g" 거래 승인 전문 응답
                            onAckResponse();
                            if (tls3800Listener != null)
                                tls3800Listener.onTLS3800ResponseCallBack(getCh(), TLS3800ResponseType.CANCEL, result);
                            break;
                        case CMD_RX_PAY_G:
                            // 부가 정보가 있는 결제로 실행 
                            chargingCurrentData = ((MainActivity) MainActivity.mContext).getChargingCurrentData(getCh());
                            //data parsing
                            System.arraycopy(buffer, 35, byteResult, 0, dataLength);
                            // 거래 구분 코드
                            // “1”[신용 승인], “2”[현금 영수증], “3”[선불 카드], “4”[제로 페이], “5”[카카오 페이(머니)] ,
                            // “6”[카카오 페이(신용)], “8”[네이버 페이] , “X”[거래 거절]:거래 매체~단말기 번호 space 채움,
                            // 취소 응답 시는 취소 요청 시 거래 구분 코드 전
                            tradeCode = new String(copyOf(byteResult, 0, 1));
                            result.put("tradeCode", tradeCode);
                            if (Objects.equals(tradeCode, "X")) {
                                dash = new String(copyOf(byteResult, 117, 1));
                                responseCode = new String(copyOf(byteResult, 118, 2));
                                result.put("responseCode", responseCode);
                                responseMessage = new String(copyOf(byteResult, 120, 37));
                                ;
                                result.put("responseMessage", responseMessage);
                                chargingCurrentData.setPrePaymentResult(false);
                            } else {
                                // 거래 매체
                                // “1”[IC], “2”[MS], “3”[RF], “4”[바코드], “5”[KEY IN],  MS/RF 무카드 취소에 대한 응답은 “2”로 응답
                                tradeMethod = new String(copyOf(byteResult, 1, 1));
                                result.put("tradeMethod", tradeMethod);
                                // 신용 카드 번호
                                creditCardNumber = new String(copyOf(byteResult, 2, 20));
                                result.put("creditCardNumber", creditCardNumber);
                                chargingCurrentData.setCreditCardNumber(creditCardNumber);
                                // 승인 금액
                                approvalAmount = new String(copyOf(byteResult, 22, 10));
                                result.put("approvalAmount", approvalAmount);
                                chargingCurrentData.setPrePayment(Integer.parseInt(approvalAmount));
                                // 세금/잔여 횟수
                                surTax = new String(copyOf(byteResult, 32, 8));
                                result.put("surTax", surTax);
                                chargingCurrentData.setSurtax(Integer.parseInt(surTax));
                                // 봉사료/사용 횟수
                                tip = new String(copyOf(byteResult, 40, 8));
                                result.put("tip", tip);
                                chargingCurrentData.setTip(Integer.parseInt(tip));
                                // 할부 개월
                                installment = new String(copyOf(byteResult, 48, 2));
                                result.put("installment", installment);
                                // 승인 번호/ 선불 카드 정보
                                approvalNumber = new String(copyOf(byteResult, 50, 12));
                                result.put("approvalNumber", approvalNumber);
                                chargingCurrentData.setApprovalNumber(approvalNumber.trim());
                                // 매출 일자
                                approvalDate = new String(copyOf(byteResult, 62, 8));
                                result.put("approvalDate", approvalDate);
                                chargingCurrentData.setApprovalDate(approvalDate);
                                // 매출 시간
                                approvalTime = new String(copyOf(byteResult, 70, 6));
                                result.put("approvalTime", approvalTime);
                                chargingCurrentData.setApprovalTime(approvalTime);
                                // 거래 고유 번호
                                // 승인 시 수신한 거래 고유 번호[거래 날짜(6)+거래 일련 번호(6)], 선불 카드 경우 카드 거래
                                tradeUniqueNumber = new String(copyOf(byteResult, 76, 12));
                                result.put("tradeUniqueNumber", tradeUniqueNumber);
                                // 가맹점 번호
                                storeNumber = new String(copyOf(byteResult, 88, 15));
                                result.put("storeNumber", storeNumber);
                                // 단말기 번호 ( 단말기 고유 번호 [단말기 ID(10)+거래 일련 번호(4)]  선불 카드의 경우 space[0x20] 채움
                                terminalNumber = new String(copyOf(byteResult, 103, 14));
                                result.put("terminalNumber", terminalNumber);
                                // 발급사
                                issuer = new String(copyOf(byteResult, 117, 20));
                                result.put("issuer", issuer);
                                // 매입사
                                buyer = new String(copyOf(byteResult, 137, 20));
                                result.put("buyer", buyer);
                                // PG 거래 일련 번호
                                pgTranSeq = new String(copyOf(byteResult, 157, 30));
                                result.put("pgTranSeq", pgTranSeq);
                                chargingCurrentData.setPgTranSeq(pgTranSeq);
                                chargingCurrentData.setPrePaymentResult(true);
                            }
                            // "g" 부가 정보 추가 거래 승인  전문 응답
                            onAckResponse();
                            if (tls3800Listener != null)
                                tls3800Listener.onTLS3800ResponseCallBack(getCh(), TLS3800ResponseType.PAYG, result);
                            break;
                        case CMD_RX_RF_READ:
                            //data parsing
                            System.arraycopy(buffer, 37, byteResult, 0, dataLength);
                            cardNumber = new String(copyOf(byteResult, 4, 16));
                            result.put("idTag", cardNumber);
                            if (tls3800Listener != null)
                                tls3800Listener.onTLS3800ResponseCallBack(getCh(), TLS3800ResponseType.RF_READ, result);
                            // "d" 카드 조회 요청 전문 응답
                            onAckResponse();
                            break;
                        case CMD_RX_IC_CHECK:
                            System.arraycopy(buffer, 35, byteResult, 0, dataLength);
                            //“O”[IC 카드 삽입], “X” 88 [IC 카드 없음]
                            result.put("icCardCheck", byteResult[0] == (byte) 0x58 ? "X" : byteResult[0] == (byte) 0x4f ? "O" : null);
                            if (tls3800Listener != null)
                                tls3800Listener.onTLS3800ResponseCallBack(getCh(), TLS3800ResponseType.CHECK, result);
                            // "m" IC 카드 체크 요청 전문 응답
                            onAckResponse();
                            break;
                        case CMD_RX_SITE_INFO:
                            System.arraycopy(buffer, 35, byteResult, 0, dataLength);
                            // 가맹점 명
                            String storeName = new String(copyOf(byteResult, 0, 32));
                            // 사업자 번호
                            String bizNo = new String(copyOf(byteResult, 32, 10));
                            //가맹점 인텍스
                            String storeIndex = new String(copyOf(byteResult, 42, 2));
                            //사이트 명
                            String siteName = new String(copyOf(byteResult, 44, 32));
                            // "4" 현장 정보 조회
                            onAckResponse();
                            break;
                    }


                } else if (Objects.equals(buffer[0], ACK)) {
                    // retry stop
                }

            }
        } catch (Exception e) {
            logger.error("TLS3800 receive read : {}", e.getMessage());
        }
    }


    private void onAckResponse() {
        try {
            byte[] ackResponse = new byte[1];
            ackResponse[0] = (byte) 0x06;
            outputStream.write(ackResponse);
        } catch (Exception e) {
            logger.error("onAckResponse error : {}", e.getMessage());
        }
    }


    public byte[] copyOf(byte[] original, int start, int newLength) {
        try {
            byte[] copy = new byte[newLength];
            System.arraycopy(original, start, copy, 0, newLength);
            return copy;
        } catch (Exception e) {
            logger.error(" copyOf Error : {}", e.getMessage());
        }
        return null;
    }


    private boolean checkData(byte[] data, int size) {
        try {
            if (Objects.equals(ETX, data[size - 2])) {
                byte xor = 0x00;
                for (int i = 0; i < size - 1; i++) {
                    xor ^= data[i];
                }
                return Objects.equals(xor, data[size - 1]);
            }
        } catch (Exception e) {
            logger.error("checkData Error : {}", e.getMessage());
        }
        return false;
    }


    public void SetState(TLS3800State state) {
        this.state = state;
    }


    public int getCh() {
        return ch;
    }

    public void setCh(int ch) {
        this.ch = ch;
    }


    public int getCancelType() {
        return cancelType;
    }

    public void setCancelType(int cancelType) {
        this.cancelType = cancelType;
    }
}
