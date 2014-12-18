package com.app.kfe.main;

import android.app.Application;
import android.content.Context;

/**
 * Klasa reprezentuj�ca obiekt aplikacji.
 * 
 * @author Damian Kaczybura
 */
public class KFE extends Application {

	private static Context _context;

	@Override
    public void onCreate() {
        super.onCreate();
        _context = this;
    }

	public static Context getContext() {
        return _context;
    }

}
