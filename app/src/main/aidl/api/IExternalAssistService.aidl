// IExternalAssistService.aidl
package api;

// Declare any non-default types here with import statements

interface IExternalAssistService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void plus();
}
