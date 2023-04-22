package de.goldendeveloper.supportmanager.errors;

import io.sentry.Sentry;

public class ExceptionHandler extends de.goldendeveloper.mysql.errors.ExceptionHandler {

    @Override
    public void callException(Exception exception) {
        Sentry.captureException(exception);
        exception.printStackTrace();
    }
}