package com.cherry.doc.util;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionFragmentHelper implements OnActivityPermissionCallback {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 2;
    private static final int REQUEST_PERMISSIONS = 1;

    @NonNull
    private final OnPermissionCallback permissionCallback;
    @NonNull private final Fragment context;
    private boolean forceAccepting;
    private boolean skipExplanation;

    private PermissionFragmentHelper(@NonNull Fragment context) {
        this.context = context;
        if (context instanceof OnPermissionCallback) {
            this.permissionCallback = (OnPermissionCallback) context;
        } else {
            throw new IllegalArgumentException("Fragment must implement (OnPermissionCallback)");
        }
    }

    private PermissionFragmentHelper(@NonNull Fragment context, @NonNull OnPermissionCallback permissionCallback) {
        this.context = context;
        this.permissionCallback = permissionCallback;
    }

    @NonNull public static PermissionFragmentHelper getInstance(@NonNull Fragment context) {
        return new PermissionFragmentHelper(context);
    }

    @NonNull public static PermissionFragmentHelper getInstance(@NonNull Fragment context, @NonNull OnPermissionCallback permissionCallback) {
        return new PermissionFragmentHelper(context, permissionCallback);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (verifyPermissions(grantResults)) {
                permissionCallback.onPermissionGranted(permissions);
            } else {
                String[] declinedPermissions = declinedPermissions(context, permissions);
                List<Boolean> deniedPermissionsLength = new ArrayList<>();
                for (String permissionName : declinedPermissions) {
                    if (permissionName != null && !isExplanationNeeded(permissionName)) {
                        permissionCallback.onPermissionReallyDeclined(permissionName);
                        deniedPermissionsLength.add(false);
                    }
                }
                if (deniedPermissionsLength.size() == 0) {
                    if (forceAccepting) {
                        requestAfterExplanation(declinedPermissions);
                        return;
                    }
                    permissionCallback.onPermissionDeclined(declinedPermissions);
                }
            }
        }
    }


    @Override public void onActivityForResult(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
                if (isSystemAlertGranted()) {
                    permissionCallback.onPermissionGranted(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});
                } else {
                    permissionCallback.onPermissionDeclined(new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});
                }
            }
        } else {
            permissionCallback.onPermissionPreGranted(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
    }


    @NonNull public PermissionFragmentHelper setForceAccepting(boolean forceAccepting) {
        this.forceAccepting = forceAccepting;
        return this;
    }


    @NonNull public PermissionFragmentHelper request(@NonNull Object permissionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionName instanceof String) {
                handleSingle((String) permissionName);
            } else if (permissionName instanceof String[]) {
                handleMulti((String[]) permissionName);
            } else {
                throw new IllegalArgumentException("Permissions can only be one of these types (String) or (String[])" +
                        ". given type is " + permissionName.getClass().getSimpleName());
            }
        } else {
            permissionCallback.onNoPermissionNeeded();
        }
        return this;
    }


    public void requestSystemAlertPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (!isSystemAlertGranted()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getContext()
                            .getPackageName()));
                    context.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                } else {
                    permissionCallback.onPermissionPreGranted(Manifest.permission.SYSTEM_ALERT_WINDOW);
                }
            } catch (Exception ignored) {}
        } else {
            permissionCallback.onPermissionPreGranted(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }
    }


    private void handleSingle(@NonNull String permissionName) {
        if (permissionExists(permissionName)) {

            if (!permissionName.equalsIgnoreCase(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (isPermissionDeclined(permissionName)) {
                    if (isExplanationNeeded(permissionName) && !skipExplanation) {
                        permissionCallback.onPermissionNeedExplanation(permissionName);
                    } else {
                        context.requestPermissions(new String[]{permissionName}, REQUEST_PERMISSIONS);
                    }
                } else {
                    permissionCallback.onPermissionPreGranted(permissionName);
                }
            } else {
                requestSystemAlertPermission();
            }
        } else {
            permissionCallback.onPermissionDeclined(new String[]{permissionName});
        }
    }


    private void handleMulti(@NonNull String[] permissionNames) {
        List<String> permissions = declinedPermissionsAsList(context, permissionNames);
        if (permissions.isEmpty()) {
            permissionCallback.onPermissionGranted(permissionNames);
            return;
        }
        boolean hasAlertWindowPermission = permissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW);
        if (hasAlertWindowPermission) {
            int index = permissions.indexOf(Manifest.permission.SYSTEM_ALERT_WINDOW);
            permissions.remove(index);
        }
        context.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSIONS);
    }


    public void requestAfterExplanation(@NonNull String permissionName) {
        if (isPermissionDeclined(permissionName)) {
            context.requestPermissions(new String[]{permissionName}, REQUEST_PERMISSIONS);
        } else {
            permissionCallback.onPermissionPreGranted(permissionName);
        }
    }


    public void requestAfterExplanation(@NonNull String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permissionName : permissions) {
            if (isPermissionDeclined(permissionName)) {
                permissionsToRequest.add(permissionName);
            } else {
                permissionCallback.onPermissionPreGranted(permissionName);
            }
        }
        if (permissionsToRequest.isEmpty()) return;
        permissions = permissionsToRequest.toArray(new String[permissionsToRequest.size()]);
        context.requestPermissions(permissions, REQUEST_PERMISSIONS);
    }


    public boolean isPermissionDeclined(@NonNull String permissionsName) {
        return ActivityCompat.checkSelfPermission(context.getContext(), permissionsName) != PackageManager.PERMISSION_GRANTED;
    }


    public boolean isPermissionGranted(@NonNull String permissionsName) {
        return ActivityCompat.checkSelfPermission(context.getContext(), permissionsName) == PackageManager.PERMISSION_GRANTED;
    }


    public boolean isExplanationNeeded(@NonNull String permissionName) {
        return context.shouldShowRequestPermissionRationale(permissionName);
    }


    public boolean isPermissionPermanentlyDenied(@NonNull String permission) {
        return isPermissionDeclined(permission) && !isExplanationNeeded(permission);
    }


    private boolean verifyPermissions(@NonNull int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    public boolean permissionExists(@NonNull String permissionName) {
        try {
            Context context = this.context.getContext();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                for (String p : packageInfo.requestedPermissions) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isSystemAlertGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context.getContext());
        }
        return true;
    }


    public void openSettingsScreen() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + context.getContext().getPackageName());
        intent.setData(uri);
        context.startActivity(intent);
    }


    @Nullable
    public static String declinedPermission(@NonNull Fragment context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (isPermissionDeclined(context, permission)) {
                return permission;
            }
        }
        return null;
    }


    public static String[] declinedPermissions(@NonNull Fragment context, @NonNull String[] permissions) {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (isPermissionDeclined(context, permission) && permissionExists(context, permission)) {
                permissionsNeeded.add(permission);
            }
        }
        return permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
    }

    public static List<String> declinedPermissionsAsList(@NonNull Fragment context, @NonNull String[] permissions) {
        List<String> permissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (isPermissionDeclined(context, permission) && permissionExists(context, permission)) {
                permissionsNeeded.add(permission);
            }
        }
        return permissionsNeeded;
    }


    public static boolean isPermissionGranted(@NonNull Fragment context, @NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }


    public static boolean isPermissionDeclined(@NonNull Fragment context, @NonNull String permission) {
        return ActivityCompat.checkSelfPermission(context.getContext(), permission) != PackageManager.PERMISSION_GRANTED;
    }


    public static boolean isExplanationNeeded(@NonNull Fragment context, @NonNull String permissionName) {
        return context.shouldShowRequestPermissionRationale(permissionName);
    }


    public static boolean isPermissionPermanentlyDenied(@NonNull Fragment context, @NonNull String permission) {
        return isPermissionDeclined(context, permission) && !isExplanationNeeded(context, permission);
    }


    public static void openSettingsScreen(@NonNull Fragment context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + context.getContext().getPackageName());
        intent.setData(uri);
        context.startActivity(intent);
    }


    public static boolean permissionExists(@NonNull Fragment context, @NonNull String permissionName) {
        try {
            Context ctx = context.getContext();
            PackageInfo packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (packageInfo.requestedPermissions != null) {
                for (String p : packageInfo.requestedPermissions) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean isSystemAlertGranted(@NonNull Fragment context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context.getContext());
        }
        return true;
    }

    public static void removeGrantedPermissions(@NonNull Fragment context, @NonNull List<PermissionModel> models) {
        List<PermissionModel> granted = new ArrayList<>();
        for (PermissionModel permissionModel : models) {
            if (permissionModel.getPermissionName().equalsIgnoreCase(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (isSystemAlertGranted(context)) {
                    granted.add(permissionModel);
                }
            } else if (isPermissionGranted(context, permissionModel.getPermissionName())) {
                granted.add(permissionModel);
            }
        }
        if (!granted.isEmpty()) {
            models.removeAll(granted);
        }
    }

    public void setSkipExplanation(boolean skipExplanation) {
        this.skipExplanation = skipExplanation;
    }
}
