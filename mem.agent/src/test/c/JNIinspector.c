
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <jvmti.h>

#include "JNIInspector.h"

/*
 * JNI methods to be implemented follow here!
 *
 */

JNIEXPORT void JNICALL Java_net_mjahn_agent_inspector_Core_nativePrint
        (JNIEnv *env, jobject obj){
    printf("\nHello World - written in C ;-)\n");

}

/*
 * Class:     net_mjahn_agent_inspector_VMInspector
 * Method:    getSizeOfAllLoadedObjectsBy
 * Signature: (Ljava/lang/ClassLoader;)J
 */
JNIEXPORT jlong JNICALL Java_net_mjahn_agent_inspector_VMInspector_getSizeOfAllLoadedObjectsBy
  (JNIEnv * env, jobject cl, jobject classLoader){
    // FIXME: implementent this!
    return 8l;
}

/*
 * Class:     net_mjahn_agent_inspector_VMInspector
 * Method:    getAllLoadedObjectsOf
 * Signature: (Ljava/lang/ClassLoader;)[Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL Java_net_mjahn_agent_inspector_VMInspector_getAllLoadedObjectsOf
  (JNIEnv * env, jobject cl, jobject classLoader){
    // FIXME: implementent this!
    return 0;
}

/*
 * Class:     net_mjahn_agent_inspector_VMInspector
 * Method:    getAllClassLoadersOfType
 * Signature: (Ljava/lang/String;)[Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL Java_net_mjahn_agent_inspector_VMInspector_getAllClassLoadersOfType
  (JNIEnv * env, jobject cl, jstring classLoaderName){
    // FIXME: implementent this!
    return 0;
}

/*
 * Class:     net_mjahn_agent_inspector_VMInspector
 * Method:    getAllClassLoaders
 * Signature: ()[Ljava/lang/ClassLoader;
 */
JNIEXPORT jobjectArray JNICALL Java_net_mjahn_agent_inspector_VMInspector_getAllClassLoaders
  (JNIEnv * env, jobject cl){
    // FIXME: implementent this!
    return 0;
}

/* Create major.minor.micro version string */
static void
version_check(jint cver, jint rver)
{
    jint cmajor, cminor, cmicro;
    jint rmajor, rminor, rmicro;

    cmajor = (cver & JVMTI_VERSION_MASK_MAJOR) >> JVMTI_VERSION_SHIFT_MAJOR;
    cminor = (cver & JVMTI_VERSION_MASK_MINOR) >> JVMTI_VERSION_SHIFT_MINOR;
    cmicro = (cver & JVMTI_VERSION_MASK_MICRO) >> JVMTI_VERSION_SHIFT_MICRO;
    rmajor = (rver & JVMTI_VERSION_MASK_MAJOR) >> JVMTI_VERSION_SHIFT_MAJOR;
    rminor = (rver & JVMTI_VERSION_MASK_MINOR) >> JVMTI_VERSION_SHIFT_MINOR;
    rmicro = (rver & JVMTI_VERSION_MASK_MICRO) >> JVMTI_VERSION_SHIFT_MICRO;
    fprintf(stdout, "Compile Time JVMTI Version: %d.%d.%d (0x%08x)\n",
			cmajor, cminor, cmicro, cver);
    fprintf(stdout, "Run Time JVMTI Version: %d.%d.%d (0x%08x)\n",
			rmajor, rminor, rmicro, rver);
    if ( cmajor != rmajor || cminor != rminor ) {
	fprintf(stderr,
	    "ERROR: Compile Time JVMTI and Run Time JVMTI are incompatible\n");
	exit(1);
    }
}


/*
 * Pure JVMTI interface implementation methods of the agent.
 *
 */

/* Callback for JVMTI_EVENT_VM_INIT */
static void JNICALL
vm_init(jvmtiEnv *jvmti, JNIEnv *env, jthread thread)
{
    jvmtiError err;
    jint       runtime_version;

    /* The exact JVMTI version doesn't have to match, however this
     *  code demonstrates how you can check that the JVMTI version seen
     *  in the jvmti.h include file matches that being supplied at runtime
     *  by the VM.
     */
    err = (*jvmti)->GetVersionNumber(jvmti, &runtime_version);
    if (err != JVMTI_ERROR_NONE) {
	fprintf(stderr, "ERROR: GetVersionNumber failed, err=%d\n", err);
	exit(1);
    } else {
        version_check(JVMTI_VERSION, runtime_version);
    }
}

/* Agent_OnLoad() is called first, we prepare for a VM_INIT event here. */
JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved)
{
    jint                rc;
    jvmtiError          err;
    jvmtiEventCallbacks callbacks;
    jvmtiEnv           *jvmti;

    /* Get JVMTI environment */
    rc = (*vm)->GetEnv(vm, (void **)&jvmti, JVMTI_VERSION);
    if (rc != JNI_OK) {
	fprintf(stderr, "ERROR: Unable to create jvmtiEnv, GetEnv failed, error=%d\n", rc);
	return -1;
    }

    /* Set callbacks and enable event notifications */
    memset(&callbacks, 0, sizeof(callbacks));
    callbacks.VMInit                  = &vm_init;
    (*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
    (*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_VM_INIT, NULL);

    return 0;
}

/* Agent_OnUnload() is called last */
JNIEXPORT void JNICALL
Agent_OnUnload(JavaVM *vm)
{
}

