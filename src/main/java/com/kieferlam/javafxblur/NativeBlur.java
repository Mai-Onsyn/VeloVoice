package com.kieferlam.javafxblur;

public class NativeBlur {

    protected NativeBlur(){}

    protected native void _extApplyBlur(String target, int accentState);

}
