package com.fromscratch.mine.bookclub.Classes;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class MyWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyFactory(this.getApplicationContext());
    }
}
