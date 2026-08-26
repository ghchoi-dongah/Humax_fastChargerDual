package com.dongah.fastcharger.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.dongah.fastcharger.MainActivity;
import com.dongah.fastcharger.basefunction.FileTransType;
import com.dongah.fastcharger.basefunction.GlobalVariables;
import com.dongah.fastcharger.websocket.ocpp.common.OccurenceConstraintException;
import com.dongah.fastcharger.websocket.ocpp.firmware.DiagnosticsStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.DiagnosticsStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.firmware.FirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.security.LogStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatus;
import com.dongah.fastcharger.websocket.ocpp.security.SignedFirmwareStatusNotificationRequest;
import com.dongah.fastcharger.websocket.ocpp.security.UploadLogStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Supplier;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SftpRxJava {

    private static final Logger logger = LoggerFactory.getLogger(SftpRxJava.class);

    private static final String LOCAL_PATH = GlobalVariables.getRootPath();

    //    private static String HOST = "211.44.234.112";
    private static String HOST = "192.168.30.120";
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "dev!1q2w3e";
    private static String REMOTE_PATH = "/app/upload/dongah/apk";
    private final CompositeDisposable disposables = new CompositeDisposable();
    FileTransType fileTransType = FileTransType.NONE;
    SftpUtil sftpUtil;
    private String VERSION_FILE_NAME = null;
    private String UI_FILE_NAME;

    FirmwareStatusNotificationRequest firmwareStatusNotificationRequest;
    DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest;
    LogStatusNotificationRequest logStatusNotificationRequest;
    SignedFirmwareStatusNotificationRequest signedFirmwareStatusNotificationRequest;

    public SftpUtil getSftpUtil() {
        return sftpUtil;
    }

    public SftpRxJava() {
        //DEVS100D12_version
        VERSION_FILE_NAME = ((MainActivity) MainActivity.mContext).getChargerConfiguration().getChargerPointModel() + "_version";
    }

    public SftpRxJava(FileTransType fileTransType, String location) {
        //DEVS100D12_version
        VERSION_FILE_NAME = ((MainActivity) MainActivity.mContext).getChargerConfiguration().getChargerPointModel() + "_version";
        this.fileTransType = fileTransType;
        UI_FILE_NAME = (fileTransType == FileTransType.FIRMWARE || fileTransType == FileTransType.SIGNED_FIRMWARE) ? "DEVS050D12.apk" :
                (fileTransType == FileTransType.DIAGNOSTICS) ? "diagnostics" : "securityLogs";
        String[] locations = locationParsing(location);
        assert locations != null;
        HOST = locations[0];
        REMOTE_PATH = locations[1] == null ? "/app/upload/dongah/apk" : "/" + locations[1];
    }

    public void downloadTask() {
        disposables.add(doInBackground().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onNext(@NonNull String s) {
                        if (Objects.equals("Success", s)) {
                            if (Objects.equals(FileTransType.FIRMWARE, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setFirmwareStatus(FirmwareStatus.Downloaded);
                                firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(FirmwareStatus.Downloaded);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.DIAGNOSTICS, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setDiagnosticsStatus(DiagnosticsStatus.Uploaded);
                                diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.Uploaded);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.SECURITY, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setUploadLogStatus(UploadLogStatus.Uploaded);
                                logStatusNotificationRequest = new LogStatusNotificationRequest(UploadLogStatus.Uploaded);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, logStatusNotificationRequest.getActionName(), logStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.SIGNED_FIRMWARE, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setSignedFirmwareStatus(SignedFirmwareStatus.Downloaded);
                                signedFirmwareStatusNotificationRequest = new SignedFirmwareStatusNotificationRequest(SignedFirmwareStatus.Downloaded);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            logger.trace("onNext( {} )", s);
                        } else {
                            if (Objects.equals(FileTransType.FIRMWARE, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setFirmwareStatus(FirmwareStatus.DownloadFailed);
                                firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(FirmwareStatus.DownloadFailed);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.DIAGNOSTICS, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setDiagnosticsStatus(DiagnosticsStatus.UploadFailed);
                                diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.UploadFailed);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.SECURITY, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setUploadLogStatus(UploadLogStatus.UploadFailure);
                                logStatusNotificationRequest = new LogStatusNotificationRequest(UploadLogStatus.UploadFailure);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, logStatusNotificationRequest.getActionName(), logStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            } else if (Objects.equals(FileTransType.SIGNED_FIRMWARE, fileTransType)) {
                                ((MainActivity) MainActivity.mContext).getChargerConfiguration().setSignedFirmwareStatus(SignedFirmwareStatus.DownloadFailed);
                                signedFirmwareStatusNotificationRequest = new SignedFirmwareStatusNotificationRequest(SignedFirmwareStatus.DownloadFailed);
                                try {
                                    ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                            .onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                                } catch (OccurenceConstraintException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            logger.trace("download fail ( {} )", s);
                        }
                    }

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (Objects.equals(FileTransType.FIRMWARE, fileTransType)) {
                            ((MainActivity) MainActivity.mContext).getChargerConfiguration().setFirmwareStatus(FirmwareStatus.DownloadFailed);
                            firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(FirmwareStatus.DownloadFailed);
                            try {
                                ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                        .onSend(100, firmwareStatusNotificationRequest.getActionName(), firmwareStatusNotificationRequest);
                            } catch (OccurenceConstraintException ea) {
                                throw new RuntimeException(ea);
                            }
                        } else if (Objects.equals(FileTransType.DIAGNOSTICS, fileTransType)) {
                            ((MainActivity) MainActivity.mContext).getChargerConfiguration().setDiagnosticsStatus(DiagnosticsStatus.UploadFailed);
                            diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.UploadFailed);
                            try {
                                ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                        .onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
                            } catch (OccurenceConstraintException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else if (Objects.equals(FileTransType.SECURITY, fileTransType)) {
                            ((MainActivity) MainActivity.mContext).getChargerConfiguration().setUploadLogStatus(UploadLogStatus.UploadFailure);
                            logStatusNotificationRequest = new LogStatusNotificationRequest(UploadLogStatus.UploadFailure);
                            try {
                                ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                        .onSend(100, logStatusNotificationRequest.getActionName(), logStatusNotificationRequest);
                            } catch (OccurenceConstraintException ez) {
                                throw new RuntimeException(ez);
                            }
                        } else if (Objects.equals(FileTransType.SIGNED_FIRMWARE, fileTransType)) {
                            ((MainActivity) MainActivity.mContext).getChargerConfiguration().setSignedFirmwareStatus(SignedFirmwareStatus.DownloadFailed);
                            signedFirmwareStatusNotificationRequest = new SignedFirmwareStatusNotificationRequest(SignedFirmwareStatus.DownloadFailed);
                            try {
                                ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                        .onSend(100, signedFirmwareStatusNotificationRequest.getActionName(), signedFirmwareStatusNotificationRequest);
                            } catch (OccurenceConstraintException xe) {
                                throw new RuntimeException(xe);
                            }
                        }
                        logger.trace(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logger.trace("onComplete");
                    }
                }));
    }


    private Observable<String> doInBackground() {
        return Observable.defer(new Supplier<ObservableSource<? extends String>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public ObservableSource<? extends String> get() throws Throwable {
                // Do some long running operation
                boolean result = false;
                sftpUtil = new SftpUtil();
                sftpUtil.sftpConnect(HOST, USER_NAME, PASSWORD);
                try {
                    if (Objects.equals(FileTransType.FIRMWARE, fileTransType) || Objects.equals(FileTransType.SIGNED_FIRMWARE, fileTransType)) {
                        result = sftpUtil.download(REMOTE_PATH, UI_FILE_NAME, LOCAL_PATH + File.separator + UI_FILE_NAME);
                    } else if (Objects.equals(FileTransType.DIAGNOSTICS, fileTransType)) {
                        ((MainActivity) MainActivity.mContext).getChargerConfiguration().setDiagnosticsStatus(DiagnosticsStatus.Uploading);
                        diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.Uploading);
                        try {
                            ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                    .onSend(100, diagnosticsStatusNotificationRequest.getActionName(), diagnosticsStatusNotificationRequest);
                        } catch (OccurenceConstraintException ex) {
                            throw new RuntimeException(ex);
                        }
                        result = sftpUtil.upload(REMOTE_PATH, new File(LOCAL_PATH + File.separator + UI_FILE_NAME));
                    } else if (Objects.equals(FileTransType.SECURITY, fileTransType)) {
                        ((MainActivity) MainActivity.mContext).getChargerConfiguration().setUploadLogStatus(UploadLogStatus.Uploading);
                        logStatusNotificationRequest = new LogStatusNotificationRequest(UploadLogStatus.Uploading);
                        try {
                            ((MainActivity) MainActivity.mContext).getSocketReceiveMessage()
                                    .onSend(100, logStatusNotificationRequest.getActionName(), logStatusNotificationRequest);
                        } catch (OccurenceConstraintException ez) {
                            throw new RuntimeException(ez);
                        }
                        result = sftpUtil.download(REMOTE_PATH, UI_FILE_NAME, LOCAL_PATH + File.separator + UI_FILE_NAME);
                    }

//                    String remoteVersion = sftpUtil.getRemoteVersion(REMOTE_PATH,VERSION_FILE_NAME);
//                    String localVersion = getVersion();
//                    if (!Objects.equals(localVersion, remoteVersion) && !Objects.equals(remoteVersion, "none")) {
//                        //버전이 틀리면.....apk download, version file update
//                        ///app/upload", "version", GlobalVariables.getRootPath() + File.separator + "version");
//                        result = sftpUtil.download(REMOTE_PATH, UI_FILE_NAME, LOCAL_PATH + File.separator +  UI_FILE_NAME);
//                        if (result) {
//                            FileManagement fileManagement = new FileManagement();
//                            fileManagement.stringToFileSave(LOCAL_PATH, VERSION_FILE_NAME,remoteVersion,false);
//                            //reboot update
//                            for (int i = 0; i < GlobalVariables.maxChannel; i++) {
//                                ((MainActivity) MainActivity.mContext).getClassUiProcess(i).getChargingCurrentData().setReBoot(result);
//                            }
//                        }
//                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    if (sftpUtil != null) {
                        sftpUtil.sftpDisconnect();
                        sftpUtil = null;
                    }
                }
                return Observable.just(result ? "Success" : "Fail");
            }
        });
    }

    /**
     * local version (storage/emulated/0/Download/version)
     *
     * @return version
     */
    private String getVersion() {
        String version = null;
        try {
            String lineStr;
            File file = new File(LOCAL_PATH + File.separator + VERSION_FILE_NAME);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((lineStr = bufferedReader.readLine()) != null) {
                    version = lineStr;
                    break;
                }
            } else {
                version = "none";
            }
        } catch (Exception e) {
            logger.error(" getVersion error : " + e.getMessage());
        }
        return version;
    }

    public void doVersionCheck() {
        try {
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("mm");
            int checkTime = Integer.parseInt(sdf.format(date)) % 15;
            if (Objects.equals(checkTime, 0)) {
                boolean versionCheck = ((MainActivity) MainActivity.mContext).getChargingCurrentData(0).isReBoot() &&
                        ((MainActivity) MainActivity.mContext).getChargingCurrentData(1).isReBoot();
                if (!versionCheck) downloadTask();
            } else {
                ((MainActivity) MainActivity.mContext).onRebooting();
            }
        } catch (Exception e) {
            logger.error("doVersionCheck error  :" + e.getMessage());
        }
    }


    private String[] locationParsing(String value) {
        try {
            //"root@192.168.30.120:/app/upload/dongah/apk";
//            String[] sourceStrings = value.split("@");
//            String hostInfo = sourceStrings[1];
//            return hostInfo.split(":");
            return value.split(":");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

}
