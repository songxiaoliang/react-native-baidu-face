package com.baidu.idl.face.platform;

/**
 * 鉴权状态
 */
public enum LicenseStatusEnum {

    StateSuccess,
    StateWarningValidityComing,
    StateErrorBegin,
    StateErrorNotFindLicense,
    StateErrorExpired,
    StateErrorAuthorized,
    StateErrorNetwork,
    StateNotInit,
    StateInitializing,
    StateUnknown;

    public static LicenseStatusEnum getLicenseStatus(int statusCode) {
        LicenseStatusEnum status = StateUnknown;
        switch (statusCode) {
            default:
                status = StateUnknown;
        }
        return status;
    }
}
