package com.box.boxjavalibv2.dao;

/**
 * This is a interface wrapping android <a href="http://developer.android.com/reference/android/os/Parcelable.html">Parcelable</a>. If you are a pure java
 * developer, you can ignore this.
 */
public interface IBoxParcelable {

    void writeToParcel(IBoxParcelWrapper parcelWrapper, int flags);
}
