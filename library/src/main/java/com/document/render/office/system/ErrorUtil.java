

package com.document.render.office.system;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.document.render.office.common.ICustomDialog;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.fc.OldFileFormatException;
import com.document.render.office.fc.poifs.filesystem.OfficeXmlFileException;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ErrorUtil {
    public static final int INSUFFICIENT_MEMORY = 0;

    public static final int SYSTEM_CRASH = 1;

    public static final int BAD_FILE = 2;

    public static final int OLD_DOCUMENT = 3;

    public static final int PARSE_ERROR = 4;

    public static final int RTF_DOCUMENT = 5;

    public static final int PASSWORD_DOCUMENT = 6;

    public static final int PASSWORD_INCORRECT = 7;

    public static final int SD_CARD_ERROR = 8;

    public static final int SD_CARD_WRITEDENIED = 9;

    public static final int SD_CARD_NOSPACELEFT = 10;

    private static final SimpleDateFormat sdf_24 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String VERSION = "2.0.0.4";

    private File logFile;

    private AlertDialog message;

    private SysKit sysKit;


    public ErrorUtil(SysKit syskit) {
        this.sysKit = syskit;

        if (syskit.getControl().getMainFrame().isWriteLog()) {
            logFile = syskit.getControl().getMainFrame().getTemporaryDirectory();
            if (logFile != null && logFile.exists() && logFile.canWrite()) {
                logFile = new File(logFile.getAbsolutePath() + File.separatorChar + "ASReader");
            } else {
                return;
            }

            if (!logFile.exists()) {
                logFile.mkdirs();
            }
            logFile = new File(logFile.getAbsolutePath() + File.separatorChar + "errorLog.txt");
        }
    }


    public void writerLog(Throwable ex) {
        writerLog(ex, false);

        ex.printStackTrace();
    }


    public void writerLog(Throwable ex, boolean isReaderFile) {
        writerLog(ex, isReaderFile, true);
    }


    public void writerLog(Throwable ex, boolean isReaderFile, boolean isShowErrorDialog) {
        try {
            if (ex instanceof AbortReaderError) {
                return;
            }

            if (logFile == null) {
                ex = new Throwable("SD CARD ERROR");
            } else if (logFile != null && logFile.exists() && !logFile.canWrite()) {
                ex = new Throwable("Write Permission denied");
            } else {
                if (sysKit.getControl().getMainFrame().isWriteLog()
                        && !(ex instanceof OutOfMemoryError)) {
                    FileWriter info = new FileWriter(logFile, true);
                    PrintWriter printWriter = new PrintWriter(info, true);
                    printWriter.println();
                    printWriter.println("--------------------------------------------------------------------------");
                    printWriter.println("Exception occurs: " + sdf_24.format(Calendar.getInstance().getTime()) + "  " + VERSION);
                    ex.printStackTrace(printWriter);
                    info.close();
                }
            }

            if (isShowErrorDialog) {
                processThrowable(ex, isReaderFile);
            }

        } catch (OutOfMemoryError e) {
            sysKit.getControl().getMainFrame().getActivity().onBackPressed();
        } catch (Exception eee) {
            return;
        }

    }


    private void processThrowable(final Throwable ex, final boolean isReaderFile) {
        final IControl control = sysKit.getControl();
        final Activity activity = control.getMainFrame().getActivity();
        if (control == null || activity == null) {
            return;
        }
        if (control.isAutoTest()) {
            System.exit(0);
        } else if (message == null) {
            control.getActivity().getWindow().getDecorView().post(new Runnable() {

                public void run() {
                    try {
                        String err = "";
                        String error = ex.toString();
                        int errorCode = SYSTEM_CRASH;
                        if (error.contains("SD")) {
                            err = control.getMainFrame().getLocalString("SD_CARD");
                            errorCode = SD_CARD_ERROR;
                        } else if (error.contains("Write Permission denied")) {
                            err = control.getMainFrame().getLocalString("SD_CARD_WRITEDENIED");
                            errorCode = SD_CARD_WRITEDENIED;
                        } else if (error.contains("No space left on device")) {
                            err = control.getMainFrame().getLocalString("SD_CARD_NOSPACELEFT");
                            errorCode = SD_CARD_NOSPACELEFT;
                        } else if (ex instanceof OutOfMemoryError || error.contains("OutOfMemoryError")) {
                            err = control.getMainFrame().getLocalString("DIALOG_INSUFFICIENT_MEMORY");
                            errorCode = INSUFFICIENT_MEMORY;
                        } else if (error.contains("no such entry")
                                || error.contains("Format error") || error.contains("Unable to read entire header")
                                || ex instanceof OfficeXmlFileException
                                || error.contains("The text piece table is corrupted")
                                || error.contains("Invalid header signature")) {
                            err = control.getMainFrame().getLocalString("DIALOG_FORMAT_ERROR");
                            errorCode = BAD_FILE;
                        } else if (error.contains("The document is really a RTF file")) {
                            err = control.getMainFrame().getLocalString("DIALOG_RTF_FILE");
                            errorCode = RTF_DOCUMENT;
                        } else if (ex instanceof OldFileFormatException) {
                            err = control.getMainFrame().getLocalString("DIALOG_OLD_DOCUMENT");
                            errorCode = OLD_DOCUMENT;
                        } else if (error.contains("Cannot process encrypted office file")) {
                            err = control.getMainFrame().getLocalString("DIALOG_CANNOT_ENCRYPTED_FILE");
                            errorCode = PASSWORD_DOCUMENT;
                        } else if (error.contains("Password is incorrect")) {
                            err = control.getMainFrame().getLocalString("DIALOG_PASSWORD_INCORRECT");
                            errorCode = PASSWORD_INCORRECT;
                        } else if (isReaderFile) {
                            err = control.getMainFrame().getLocalString("DIALOG_PARSE_ERROR");
                            errorCode = PARSE_ERROR;
                        } else if (ex instanceof IllegalArgumentException
                                || ex instanceof ClassCastException) {
                            System.out.println("on dialog cash//" + ex.getLocalizedMessage());
                            System.out.println("on dialog cash//" + ex.getMessage());
                            err = control.getMainFrame().getLocalString("DIALOG_SYSTEM_CRASH");
                            errorCode = SYSTEM_CRASH;
                        } else if (sysKit.isDebug()) {
                            System.out.println("on dialog cash syskit//" + ex.getLocalizedMessage());
                            System.out.println("on dialog cash syskit//" + ex.getMessage());
                            err = control.getMainFrame().getLocalString("DIALOG_SYSTEM_CRASH");
                        }
                        if (err.length() > 0) {

                            control.getMainFrame().error(errorCode);
                            control.actionEvent(EventConstant.APP_ABORTREADING, true);
                            if (control.getMainFrame().isPopUpErrorDlg() && message == null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setMessage(err);
                                builder.setCancelable(false);
                                builder.setTitle(control.getMainFrame().getAppName());
                                String ok = control.getMainFrame().getLocalString("BUTTON_OK");
                                builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        message = null;


                                        activity.onBackPressed();

                                    }
                                });
                                message = builder.create();
                                message.show();
                            } else {
                                ICustomDialog dlgListener = control.getCustomDialog();
                                if (dlgListener != null) {
                                    dlgListener.showDialog(ICustomDialog.DIALOGTYPE_ERROR);
                                }
                            }
                        }

                    } catch (Exception eee) {
                        eee.printStackTrace();
                    }
                }
            });
        }
    }


    public void dispose() {
        sysKit = null;
    }

}
