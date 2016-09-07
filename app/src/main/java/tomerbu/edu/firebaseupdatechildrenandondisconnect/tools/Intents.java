package tomerbu.edu.firebaseupdatechildrenandondisconnect.tools;

import android.content.Context;
import android.content.Intent;

import tomerbu.edu.firebaseupdatechildrenandondisconnect.LoginActivity;
import tomerbu.edu.firebaseupdatechildrenandondisconnect.MainActivity;

/**
 * Created by tomerbuzaglo on 07/09/2016.
 * Copyright 2016 tomerbuzaglo. All Rights Reserved
 * <p/>
 * Licensed under the Apache License, Version 2.0
 * you may not use this file except
 * in compliance with the License
 */
public class Intents {
    public static void gotoLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void gotoMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
