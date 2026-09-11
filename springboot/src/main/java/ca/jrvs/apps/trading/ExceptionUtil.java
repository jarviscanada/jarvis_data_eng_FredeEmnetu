package ca.jrvs.apps.trading;

public class ExceptionUtil  {

    public static String buildMessage(String message) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0 = getStackTrace(), 1 = buildMessage(), 2 = constructor, 3 = actual caller
        StackTraceElement origin = stack.length > 3 ? stack[3] : null;
        String location = origin != null
            ? origin.getClassName() + "." + origin.getMethodName() + "()"
            : "unknown";
        return message + " in " + location;
    }
}
