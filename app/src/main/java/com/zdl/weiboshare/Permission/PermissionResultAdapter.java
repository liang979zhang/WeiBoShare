package com.zdl.weiboshare.Permission;

/**
 * Created by 89667 on 2018/3/14.
 * 支持任意重写方法,而无需重写所有的方法
 */

public class PermissionResultAdapter implements PermissionResultCallBack {
    @Override
    public void onPermissionGranted() {

    }

    @Override
    public void onPermissionGranted(String... permissions) {

    }

    @Override
    public void onPermissionDenied(String... permissions) {

    }

    @Override
    public void onRationalShow(String... permissions) {

    }
}
