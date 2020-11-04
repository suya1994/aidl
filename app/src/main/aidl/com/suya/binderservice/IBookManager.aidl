// IBookManager.aidl
package com.suya.binderservice;

// Declare any non-default types here with import statements

import com.suya.binderservice.IBook;
import com.suya.binderservice.IOnNewBookArrivedlInterface;

interface IBookManager {
   List<Book> getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBookArrivedlInterface listener);
   void unregisterListener(IOnNewBookArrivedlInterface listener);
}