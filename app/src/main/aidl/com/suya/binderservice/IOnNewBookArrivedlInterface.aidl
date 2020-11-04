// IOnNewBookArrivedlInterface.aidl
package com.suya.binderservice;

// Declare any non-default types here with import statements

import com.suya.binderservice.IBook;

interface IOnNewBookArrivedlInterface {
    void onNewBookArrieved(in Book newBook);
}