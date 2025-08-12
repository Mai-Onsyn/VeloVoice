# Version History

Migrated from SourceForge and udated.

## JACOB 1.20

### What's New

* Upgraded from VS2015 to VS 2019
* Move from Sourceforge to GitHub
* Updated Junit jars
* VT_DATE and SafeArray
* Built with Java 8

### Tracked Changes

| Item                 | Description                                    |
| -------------------- | ---------------------------------------------- |
| **Bugs**             |                                                |
|                      | none                                           |
| **Patches**          |                                                |
| 48                   | Update to VS2019 Community and Windows 10 libs |
| 1                    | Support VT_DATE getting from SafeArray         |
| **Feature Requests** |                                                |
|                      | none                                           |

## JACOB 1.19

### What's New

* Upgraded from Java 6 to Java 8 compilation
* Upgraded from junit 3.8.1 to 4.12
* Migrated from CVS to GIT using sourceforge migration instructions https://sourceforge.net/p/forge/documentation/CVS/

### Tracked Changes

| Item                 | Description                                                |
| -------------------- | ---------------------------------------------------------- |
| **Bugs**             |                                                            |
| 132                  | 32 bit ponters not convertd to 64 bit                      |
| 130                  | Name value incorrect in Mainfest.MF affecting tamper check |
| **Patches**          |                                                            |
|                      | none                                                       |
| **Feature Requests** |                                                            |
|                      | none                                                       |

## JACOB 1.18

### What's New

* (M2) Built with Java 1.6 and Visual Studio 2013 Microsoft Platform SDK V7.1A (introduced with VS2012)instead of v7.0A (vs2010) Targeting SDK V7.1 with USING_V110_SDK71" http://en.wikipedia.org/wiki/Microsoft_Windows_SDK
* (M2) Temporarily using AMD64 compiler instead of x86_amd64 because of installation issues on dev machine. Should generate same output even though dll files are different sizes between M1 and M2\. http://msdn.microsoft.com/en-us/library/x4d2c09s.aspx
* (M2) Dropped support for XP

### Tracked Changes

| Item                 | Description                                                     |
| -------------------- | --------------------------------------------------------------- |
| **Bugs**             |                                                                 |
| 119 (new numbers)    | (M?)Attribute lacking in MANIFEST.MF required since Java 1.7u45 |
| **Patches**          |                                                                 |
| 42 (new numbers)     | (M3)Mapping of names to dispIDs implemented                     |
| **Feature Requests** |                                                                 |
|                      | none                                                            |

## JACOB 1.17

### What's New

* No new features

### Tracked Changes

| Item                 | Description                                                                                               |
| -------------------- | --------------------------------------------------------------------------------------------------------- |
| *Bugs**              |                                                                                                           |
| 3505940              | (M2)JacobVersion.properties accidently omitted from JAR. Must build from command line to get it included. |
| 3436102              | (M1)Fix memory pointer that was 32 bit. Causes defects in 64 bit systems above 2GB                        |
| 115 (new numbers)    | (M3)Release problem if you've got two threads with the same name                                          |
| 111 (new numbers)    | (M3)m_pDispatch is not 0 if not attached                                                                  |
| 117 (new numbers)    | (M4) NullPointerException injacob-1.17-M2                                                                 |
| **Patches**          |                                                                                                           |
| 41 (new numbers)     | (M3)Fix for SafeArray(String) constructor                                                                 |
| **Feature Requests** |                                                                                                           |
|                      | none                                                                                                      |

## JACOB 1.16

### What's New

* No new features

### Tracked Changes
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">3436143</td>
<td width="87%" valign="top">(M2)Remove Main-Class from MANIFEST because there is no main class entry point for this jar file</td>
</tr>
<tr>
<td width="13%" valign="top">3435567</td>
<td width="87%" valign="top">(M2)Add HRESULT to error message when unknown COM error raised in Dispatch</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M2)Added debug info to advise failure messages.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M2)Added support for null dispatch object in putVariantDispatch.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M2)Fixed memory leak in Variant.cpp zeroVariant method possibly related to previous fix proposed in SF 1689061 but never implemented. I guess we should fix it since people keep pointing it out</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M2)Variant.getString() now returns null for NULL or EMPTY Variants instead of throwing exception.</td>
</tr>
<tr>
<td width="13%" valign="top">3412922</td>
<td width="87%" valign="top">(M1)Fix for: When a DispatchEvent is created with a COM object, the COM object is never released totally, and the destructor function is never called.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">3377279</td>
<td width="87%" valign="top">(M1)Fix possible exception. Added initializing Variant used to retrieve the next value from IEnum because some implementations call VariantClear on it before setting a new value</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M1)Isolate compilation of SafeArrayTest.java because it is UTF-16 and not UTF-8</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M1)Changed windows version _WIN32_WINNT to 0x0500 to fix build with VS2010\. (now 2000 or later)</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M1)Fixed the leak in Java_com_jacob_com_Variant_putVariantNoParam</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top">(M1)Added Dispatch.Method to the invoke flags to call _NewEnum. There are some non-conforming legacy implementations that expose _NewEnum as a method.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top"></td>
</tr>
</tbody>
</table>

## JACOB 1.15

### What's New

* MS Libraries are now statically linked using /MT instead of /MD to reduce issues library compatibility issues, especially on older platforms. The VC++ redistributable library no longer needs to be installed as a stand alone product.

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">3065265</td>
<td width="87%" valign="top">(M4)Bit masking in Variant.getXXXRef() uses wrong mask allowing more than one type to be seen as the requested type. Code that passed in the correct type always worked but invalid types were not always detected.</td>
</tr>
<tr>
<td width="13%" valign="top">2935662</td>
<td width="87%" valign="top">(M4)Error handling code crashes because of uninitialized data in Dispatch.cpp Check for NULL fails. pfnDeferredFillIn pointer is not initialized, but it's not NULL.</td>
</tr>
<tr>
<td width="13%" valign="top">2819445</td>
<td width="87%" valign="top">(M3)SafeArray.fromLongArray fails when using VariantLongInt</td>
</tr>
<tr>
<td width="13%" valign="top">2847577</td>
<td width="87%" valign="top">(M3) SafeArray#setString(*) incorrectly handles unicode strings</td>
</tr>
<tr>
<td width="13%" valign="top">2721937</td>
<td width="87%" valign="top">(M2)System.getProperties call caused security exception in applet. _com.jacob.includeAllClassesInROT_ now acts as master switch for class/ROT control. This change also has the side benefit that the PutInROT property is not checked on every object creation for users who run in the standard _all classes in ROT_ mode.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">2762275</td>
<td width="87%" valign="top">(M1)Support conversion of primitive arrays to Variant arrays.</td>
</tr>
<tr>
<td width="13%" valign="top">2171967</td>
<td width="87%" valign="top">(M1)VariantUtils.populateVariant can cause VM crash with unrecognized type.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">3137337</td>
<td width="87%" valign="top">Add JNLP Applet example. Read the README to understand what jar file signing and file placement is required</td>
</tr>
<tr>
<td width="13%" valign="top">2963102</td>
<td width="87%" valign="top">(M4)Convert API to use var args and remove the many overloaded Dispatch methods that each added one more parameter.</td>
</tr>
<tr>
<td width="13%" valign="top">2927058</td>
<td width="87%" valign="top">(M4)a hasExited() method that allows polling until a COM server is terminated and implemented this method in JACOB</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top"></td>
</tr>
</tbody>
</table>

## JACOB 1.14.3

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">2011706</td>
<td width="87%" valign="top">Fixed windows memory corruption unhooking call back proxy</td>
</tr>
<tr>
<td width="13%" valign="top">1986987</td>
<td width="87%" valign="top">Possible deadlock when multiple threads starting and stopping that rely on implicit ComThread.InitMTA</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top"></td>
</tr>
</tbody>
</table>

## JACOB 1.14.1

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1919441</td>
<td width="87%" valign="top">Type: loading 64 bit jacob.dll. Computed dll name includes space that is not in name of actual dll</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top"></td>
</tr>
</tbody>
</table>

## JACOB 1.14

### What's New

* Binaries are now compiled with Java 5\. JDK 1.4 support dropped.
* Jacob now loads dlls based on platform (32 bit /64 bit) and version number.
### Tracked Changes
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1857439</td>
<td width="87%" valign="top">(M7) version.properties renamed to META-INF/JacobVersion.properties to remove collision with WebSphere version.properties.</td>
</tr>
<tr>
<td width="13%" valign="top">1840487</td>
<td width="87%" valign="top">(M6) toJavaObject() converting to SafeArray did shallow copy that left two objects pointing at the same windows memory.</td>
</tr>
<tr>
<td width="13%" valign="top">1829201</td>
<td width="87%" valign="top">(M5) DECIMAL rounding behavior externalized and old Variant decimal API restored.</td>
</tr>
<tr>
<td width="13%" valign="top">1829201</td>
<td width="87%" valign="top">(M5) DECIMAL rounding behavior externalized and old Variant decimal API restored.</td>
</tr>
<tr>
<td width="13%" valign="top">1829201</td>
<td width="87%" valign="top">(M4) Decimal type now throws IllegalArgumentException when more than 12 bytes worth the digits exist in BigDecimal. Rounding support added to reduce precision of BigDecimals when converting into VT_DECIMAL</td>
</tr>
<tr>
<td width="13%" valign="top">1815163</td>
<td width="87%" valign="top">(M2) Double and Decimal conversion to Int fails for negative values</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1845039</td>
<td width="87%" valign="top">(M7) Jacob DLL name can now be customized to support bundling of Jacob in other products.</td>
</tr>
<tr>
<td width="13%" valign="top">1845039</td>
<td width="87%" valign="top">(M6) Jacob DLL names are now qualified by platform and release. The JacobLibraryLoader now determines the correct 32bit or 64bit dll based on the system architecture. Jacob.jar now also knows the version of the dll it is looking for (by name) and loads the correct one. JWS clients will have to modify their dll loaders. See: The JWS classloader sample</td>
</tr>
<tr>
<td width="13%" valign="top">1828371</td>
<td width="87%" valign="top">(M4) Added VT_I8 support to SafeArray.</td>
</tr>
<tr>
<td width="13%" valign="top">1813458</td>
<td width="87%" valign="top">(M3) Expand type support. Changed currency support to use new Currency class. Added VT_I8 64 bit support. VT_I8 support requires Windows XP or later. VT_I8 not supported by windows in Windows 2000 and earlier. Added more primitive constructors to Variant.</td>
</tr>
<tr>
<td width="13%" valign="top">1816863</td>
<td width="87%" valign="top">(M1) Migrate Jacob to JDK 5</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top">This is now built with Java 5 compiler and Java 5 syntax. This release is not compatible with JDK 1.4.x and earlier.</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top">This release requires the Visual C++ 2005 libraries. See 1.13 Known Issues for more information.</td>
</tr>
</tbody>
</table>

## JACOB 1.13

### What's New

* Binaries compiled with with Visual Studio 2005 in place of VC98\.
* Changed milestone release naming convention from "pre..." to "M..."
* The unittest directory now a JUnit 3.8.1 test repository

### Tracked Changes
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1793362</td>
<td width="87%" valign="top">(M5) ERROR_MORE_DATA causes failures. Fix submitted for defect found while porting Jameleon to use current release of Jacob.</td>
</tr>
<tr>
<td width="13%" valign="top">1775889</td>
<td width="87%" valign="top">(M4) Fixed leak SafeArray setString(int[],value) and other setString() methods</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">1794811</td>
<td width="87%" valign="top">(M5) Support Unicode strings in COM failure messages</td>
</tr>
<tr>
<td width="13%" valign="top">1793346</td>
<td width="87%" valign="top">(M5) Replaced use of deprecated API and removed unused variables.</td>
</tr>
<tr>
<td width="13%" valign="top">1701995</td>
<td width="87%" valign="top">(M2) Added option to exclude classes from ROT to try and manage memory in heavy event callback programs. Feature is 100% backwards compatible by default.</td>
</tr>
<tr>
<td width="13%" valign="top">1709841</td>
<td width="87%" valign="top">(M1) Compiled with Visual Studio 2005\. Jacob now requires 2005 or later libraries. See the UsingJacob.html file for impact this has on NT, 2000 and Server 2003 users.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1772783</td>
<td width="87%" valign="top">(M4) Added VT_DECIMAL support for BigDecimals whose scale less than 28</td>
</tr>
<tr>
<td width="13%" valign="top">1761727</td>
<td width="87%" valign="top">(M3) unittest directory test programs converted to JUnit 3.8.1\. New ANT target created to run all unit tests.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top"></td>
<td width="87%" valign="top">Jacob 1.13 is built using VC++ 2005. That creates a dependency on the Visual C++ 2005 libraries and msvcr80.dll. This library is normally installed on XP systems but may have to be manually installed on older systems. The pagackage, often referred to as vcredist.exe can be obtained from the MS downloads site. If you are getting loader errors on this release or later when loading the dll then you may be missing this library.</td>
</tr>
</tbody>
</table>

## JACOB 1.12

### What's New

* Now compiles with with Visual versions later than VC 98

### Tracked Changes
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1651926</td>
<td width="87%" valign="top">(pre-release 1) ActiveXComponent: getPropertyAsString should call getString() instead of toString()</td>
</tr>
<tr>
<td width="13%" valign="top">1569864</td>
<td width="87%" valign="top">(pre-release 1) IEnumVariant leak fixed in patch 1674179</td>
</tr>
<tr>
<td width="13%" valign="top">1112667</td>
<td width="87%" valign="top">(pre-release 1) IEnumVariant leak fixed in patch 1674179</td>
</tr>
<tr>
<td width="13%" valign="top">1465539</td>
<td width="87%" valign="top">(pre-release 1) IEnumVariant leak fixed in patch 1674179</td>
</tr>
<tr>
<td width="13%" valign="top">1699946</td>
<td width="87%" valign="top">(pre-release 5) Unrecognized event callback id leaves thread attached to VM when ComFailException is thrown.</td>
</tr>
<tr>
<td width="13%" valign="top">1699965</td>
<td width="87%" valign="top">(pre-release 5) toJavaObject() fails for Dispatch because getDispatchRef() called instead of getDispatch().</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">1674015</td>
<td width="87%" valign="top">(pre-release 1) ROT hashmap key generation when autogc=no (default) can lead to key collisions in hashmap. This causes objects to be garbage finalized when they shouldn't be resulting in vm failures with large (large) numbers of objects.</td>
</tr>
<tr>
<td width="13%" valign="top">1674179</td>
<td width="87%" valign="top">(pre-release 1) Fixed Enum leaks with EnumVariants in Variant.cpp and EnumVariant.cpp</td>
</tr>
<tr>
<td width="13%" valign="top">1687419</td>
<td width="87%" valign="top">(pre-release 3) Corrected calls to AttachCurrentThread in EventProxy</td>
</tr>
<tr>
<td width="13%" valign="top">1689061</td>
<td width="87%" valign="top">(pre-release 4) C code changes to fix VC2003 compiler warnings.</td>
</tr>
<tr>
<td width="13%" valign="top">1690420</td>
<td width="87%" valign="top">(pre-release 4) Incorrect memcpy lengths for Currency Variants</td>
</tr>
<tr>
<td width="13%" valign="top">1650134</td>
<td width="87%" valign="top">(pre-release 6) Beta support for VT_VARIANT (VariantVariant). Includes support for putVariant, getVariant, toJavaObject, Variant(Object,flag). Enclosed variants are retreived as Java objects.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1662887</td>
<td width="87%" valign="top">(pre-release 1) Dispatch static methods should throw runtime exceptions when null is passed in for the Dispatch object and when the Dispatch object is in an invalid state.</td>
</tr>
<tr>
<td width="13%" valign="top">1702604</td>
<td width="87%" valign="top">(pre-release 6) Support java semantics in event callbacks. Create ActiveXInvocationProxy and ActiveXDispatchEvents that provide the supplemental API. See IETestActiveXProxy.java for an example.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Known Issues**</td>
</tr>
<tr>
<td width="13%" valign="top">1504120</td>
<td width="87%" valign="top">_Identified 1.11.1_ Microsoft 2003 Server crashes. Server crashes on MS 2003 Server have been reported over time. Some users have had say that Jacob must be built on MS2003 Server to run reliably. The Jacob distribution on Sourceforge is built on Windows XP SP2</td>
</tr>
<tr>
<td width="13%" valign="top">1677933</td>
<td width="87%" valign="top">_Identified 1.11.1_ Process affinity may have to be set on dual core machines to avoid com exceptions.</td>
</tr>
<tr>
<td width="13%" valign="top">no ticket</td>
<td width="87%" valign="top">_Identified 1.11.1_ Versions of JDK 1.5 prior to 1.5_10 are known to leak JNI handles. See the Sun bug tracking system http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6399321 for more details.</td>
</tr>
</tbody>
</table>

## JACOB 1.11.1

### What's New

Bug fix release

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1602188</td>
<td width="87%" valign="top">Variant toString() causes stack overflow for byRef() Variants (This show stopper defect forced the 1.11.1 release)</td>
</tr>
<tr>
<td width="13%" valign="top">1611487</td>
<td width="87%" valign="top">Variant toJavaObject() doesn't work for byRef Variables and returned the wrong value for unrecognized Variant types.</td>
</tr>
<tr>
<td width="13%" valign="top">1607878</td>
<td width="87%" valign="top">Variant getJavaDateRef() fails.</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">SF1603631</td>
<td width="87%" valign="top">Concurrent modification of ROT causes VM crashes. Access to the ROT has been sychronized. No performance impact analysis has been done</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
</tbody>
</table>

## JACOB 1.11

### What's New

* **Build**
  * Build process now notifies developer if version property missing
  * Build process now compiles 32 bit and/or 64 bit DLLs as appropriate for the build environment.
* **API Changes**
  * Variant.noParam() changed to Variant.putNoParam()
  * Variant.toString() now follows normal java semantics. This conflicted with the jacob toXXX() standard.
  * Many Variant.toXXX() did type conversion in addition to a get. The methods have deprecated because folks didn't realize they were doing type conversion. Most calls to toXXX() methods should actually be getXXX() calls. This also allowed toString() to follow Java conventions
  * Variant native methods are wrapped with java methods that do parameter and state checking to better armor the Variant

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1550604</td>
<td width="87%" valign="top">Build process died with confusing error if version not set in properties file(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1511033</td>
<td width="87%" valign="top">Fix array index out of bounds problem due to coding error (pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1570270</td>
<td width="87%" valign="top">~Event method in EventProxy may unhook java thread from VM. Can get JNI error because unhooking listner detatched Java VM thread (pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1538011</td>
<td width="87%" valign="top">toString() non compliant with java standards. The toString() method converted the underlying data to a string and it shouldn't. This caused a rethinking of all toXXX() methods other than toDispatch(). Most of the toXXX() methods have now been deprecated and should be replaced with getXXX() methods. (pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1478162</td>
<td width="87%" valign="top">Variant does not warn user if methods called after released. All putXXX() and getXXX() methods now check to see if they've been released prior to calling the JNI code. toXXX() methods are deprecated but protected in the same way.(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">SF1493647</td>
<td width="87%" valign="top">Support command line parameter dll location specification. Applets and other tools can now specificy the dll location that is fed to a System.load() rather than System.loadLibrary for the situation where the app can't write the dll to a library path directory.(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1580993</td>
<td width="87%" valign="top">Modify the build process to support 64 bit dll construction(pre2)</td>
</tr>
<tr>
<td width="13%" valign="top">1550628</td>
<td width="87%" valign="top">Moved all LoadLibrary requests into LibraryLoader. Classes not subclassed off of JacobObject make calls to a static method on LibraryLoader to make sure DLL is loaded(pre1)</td>
</tr>
</tbody>
</table>

## JACOB 1.10.1

### What's New

* **Variants**
  * Static constnats are no longer released
  * obj2variant on Dispatch now supports passing a java.util.Date

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1474474</td>
<td width="87%" valign="top">Static constants in the Variant class can no longer have SafeRelease() called on them.</td>
</tr>
<tr>
<td width="13%" valign="top">1477793</td>
<td width="87%" valign="top">obj2variant should accept java.util.Date the same way the Variant constructor does</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
</tbody>
</table>

## JACOB 1.10

### What's New

* **Windows Processes**
  * New proposed API to support ActiveXComponent connections to already running applications. Factory methods have been added to the ActiveXComponent to support this. (Feedback wanted)
* **Variants**
  * Variant now accept Java Dates in the constructor.
  * Redundant constructors removed
  * Experimental toJavaObject() method added that automatically converts to appropriate java type
  * Support added for "NOTHING", a Variant of type Dispatch with no value
  * Non functional getNull() and getEmpty() methods deprecated. They were void methods
* **Event Callbacks**
  * Jacob normally uses information in the registry to find the connection information needed to set up event callbacks. Excel and other programs don't put that information in the registry. A new optional parameter has been added to the DispatchEvents constructors that lets a user provide the location of the OLB or EXE that contains the information required to retrieve the events.
  * Event handlers can now return a Variant to calling MS Windows program. Event handlers that do not return an object should still be defined as being of type "void". This means that support for event handler's returning values should be backwards compatible with previous releases.
### Tracked Changes
<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1340233</td>
<td width="87%" valign="top">Null Program Id in Dispatch Constructor does bad things to VM(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1341763</td>
<td width="87%" valign="top">Removed Variant serializable interface because it is not actually serializable on 2000/xp(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1435215</td>
<td width="87%" valign="top">Incorrect memory release in SafeArray.cpp (pre4)</td>
</tr>
<tr>
<td width="13%" valign="top">1224219</td>
<td width="87%" valign="top">Memory leak in SafeArray.GetString() Olivier Laurent, Software AG Luxembourg and David Pierron, Software AG Luxembourg (pre3)</td>
</tr>
<tr>
<td width="13%" valign="top">1224219</td>
<td width="87%" valign="top">Change from UTF to UNICODE SafeArray.fromStringArray() Olivier Laurent, Software AG Luxembourg and David Pierron, Software AG Luxembourg (pre3)</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">1386454</td>
<td width="87%" valign="top">Return values from event callbacks (pre3)</td>
</tr>
<tr>
<td width="13%" valign="top">1394001</td>
<td width="87%" valign="top">Missing variable initialization (pre3)</td>
</tr>
<tr>
<td width="13%" valign="top">1208570</td>
<td width="87%" valign="top">Support Excel and other objects events(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1241037</td>
<td width="87%" valign="top">Support NULL VT_DISPATCH.(pre2)</td>
</tr>
<tr>
<td width="13%" valign="top">1169851</td>
<td width="87%" valign="top">Support of VB's Nothing.(pre2)</td>
</tr>
<tr>
<td width="13%" valign="top">1185167</td>
<td width="87%" valign="top">Provide methods to connect to running instance.(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">959381</td>
<td width="87%" valign="top">Auto variant to object conversion method method added to Variant.(pre1)</td>
</tr>
<tr>
<td width="13%" valign="top">1341779</td>
<td width="87%" valign="top">Variant should accept java.util.Date in Variant(Object) constructor(pre1)</td>
</tr>
</tbody>
</table>

## JACOB 1.9.1

### What's New

* **License**
  * License standardized on LGPL. See LICENSE.TXT for more information
* **Event Callbacks**
  * Event handlers are now wrapped in an InvocationProxy. The COM/JNI event code knows only about InvocationProxies and calls the appropriate methods on the Invocation proxy to get needed Variant instances and to forward events to the Java layer.
* **Variants**
  * Automatic conversion between Windows Time and Java Date is now supported in Variant object.
* **SafeArray**
  * Multi-dimensional (greater than 2) support

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1335897</td>
<td width="87%" valign="top">SafeArray() called toString() on objects when debug was turned on</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">1314116</td>
<td width="87%" valign="top">putString UNICODE string length not detected</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1264850</td>
<td width="87%" valign="top">Custom Event Dispatching</td>
</tr>
<tr>
<td width="13%" valign="top">959382</td>
<td width="87%" valign="top">Time Conversion</td>
</tr>
<tr>
<td width="13%" valign="top">1313892</td>
<td width="87%" valign="top">Multi-Dimensional SafeArray</td>
</tr>
</tbody>
</table>

## JACOB 1.9

### What's New

* **Event Callbacks**
  * Variant parameters can now be modified by the receiver to be passed back to the COM caller
  * Callbacks now create objects of class VariantViaEvent rather than Variant. This was aided to add debugging and tracing
  * Callbacks can now be received when running in JWS other launchers where JACOB.jar is not in the system classloader's path.
* **Dispatch API Clarifications**
  * All static method's first parameters have been more strongly typed to the Dispatch class, rather than Object. This may call for code changes in the cases of code that just asigned Dispatch objects to variables of type Object rather than Dispatch or one of its subclasses
* **Dispatch subclasses are now supported with pointer modifying constructor**
  * Dispatch and ActiveXComponent now includes a constructor to be used by Dispatch subclasses that swaps the pointers around. This eliminates the need for every Dispatch subclass to have a constructor that swapped and nulled out the pointers to the COM layer. All samples have updated to use the new api
* **ActiveXComponent has been upgraded**
  * ActiveXComponent methods return ActiveXComponets
  * Methods have been added to the ActiveXComponents to retrieve parameters as Dispatch objects or ActiveX components. The Script Tests have been updated to show the same programs in Dispatch format or ActiveXComponentFormat
* **Memory Management**
  * Beta test option that lets an application use automatic object object removal through the use of weak reference hash maps in the ROT class.  The default behavior of manual release via the COMThread class has been retained as the default behavior.  Developers can test automatic memory collection by using the command line option _-Dcom.JACOB.autogc=true_
* **JNI Changes**
  * Erroneous Array dimension checking fixed for certain boolean set and get functions
  * Alternative method for finding Variant class for callbacks in JWS or other application lanchers where the system classloader does not know about JACOB classes.
  * Unicode is supported for putString and putStringRef
  * EventProxy zeros out the com object reference in the Variant objects that are created by EventProxy so that they are not double released, by both the Java VM and calling code from the COM side. The caller is supposed to be responsible for releasing the memory it created. This fix only applies to Variants created in callbacks.
* **Logging Additions**
  * Debugging logging to standard out for JACOB can be turned on by using the command line option _-Dcom.JACOB.debug=true_
* **Visual Studio**
  * The VisualStudio directory in the CVS repository will be removed in the next release
* **Documentation**
  * API documentation via Javadoc  is now being generated for all classes.
  * The development team is looking for help in upgrading the quality of the class documentation
* **Build Changes**
  * A static method has been added to JacobObject that returns the build version
  * The project is now being built using ANT.  Most of the developers are running this from inside of Eclipse
  * All makefiles have been purged

### Tracked Changes

<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" bordercolor="#111111" width="100%">
<tbody>
<tr>
<td colspan="2">**Bugs**</td>
</tr>
<tr>
<td width="13%" valign="top">1116101</td>
<td width="87%" valign="top">jacob-msg 0284 : Access Violation while garbage collecting</td>
</tr>
<tr>
<td width="13%" valign="top">1114159</td>
<td width="87%" valign="top">Problem with COM Error Trapping in JACOB DLL</td>
</tr>
<tr>
<td width="13%" valign="top">1113610</td>
<td width="87%" valign="top">Bad error check in SafeArray.cpp</td>
</tr>
<tr>
<td width="13%" valign="top">1066698</td>
<td width="87%" valign="top">Minor Memory leak in Dispatch.cpp</td>
</tr>
<tr>
<td width="13%" valign="top">1065533</td>
<td width="87%" valign="top">Problem with unicode conversion</td>
</tr>
<tr>
<td width="13%" valign="top">1053871</td>
<td width="87%" valign="top">solution for memory leak in 1.7</td>
</tr>
<tr>
<td width="13%" valign="top">1053870</td>
<td width="87%" valign="top">JACOB0msg 2019 - Safe Array</td>
</tr>
<tr>
<td width="13%" valign="top">1053866</td>
<td width="87%" valign="top">getHResult only returns 80020009</td>
</tr>
<tr>
<td width="13%" valign="top">960646</td>
<td width="87%" valign="top">But in SafeArray:: getBoolean for 2D arrays</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Patches**</td>
</tr>
<tr>
<td width="13%" valign="top">1115187</td>
<td width="87%" valign="top">EventCallbacks fail w/Variant ClassNotFoundException in JWS</td>
</tr>
<tr>
<td width="13%" valign="top">1105915</td>
<td width="87%" valign="top">Fix for event handling memory corruption</td>
</tr>
<tr>
<td width="13%" valign="top">1090104</td>
<td width="87%" valign="top">Weak Reference in teh ROT</td>
</tr>
<tr>
<td width="13%" valign="top">1068544</td>
<td width="87%" valign="top">in/out parameter support for event handlers</td>
</tr>
<tr>
<td width="13%" valign="top">981540</td>
<td width="87%" valign="top">jre 1.4.2 fix as patch</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
<tr>
<td colspan="2">**Feature Requests**</td>
</tr>
<tr>
<td width="13%" valign="top">1049390</td>
<td width="87%" valign="top">static Version information</td>
</tr>
<tr>
<td width="13%" valign="top">1049224</td>
<td width="87%" valign="top">Javadocs or at least script to generate it</td>
</tr>
<tr>
<td width="13%" valign="top">1049158</td>
<td width="87%" valign="top">API to get ProgId of ActiveXComponent</td>
</tr>
<tr>
<td width="13%" valign="top"> </td>
<td width="87%" valign="top"> </td>
</tr>
</tbody>
</table>

## JACOB 1.8

### What's New

* **Move To SourceForge** The project is not housed at [Sourceforge.net](http://sourceforge.net/projects/jacob-project/).
* **Licensing Change** All limitations on commercial use of JACOB have been removed and it is now being developed under a BSD license at [Sourceforge.net](http://sourceforge.net/projects/jacob-project/).
* **Compiled with Java 1.4.2** Version 1.8 was compiled with JSEE 1.4.2 and fixes the compilation bug that was remnant of compilation with JDK 1.1.
* **Baseline For Change** This version is the baseline for the first CVS checkin and we encourage people to start contributing to the project with this version.

* ## JACOB 1.7

    ### What's New

  * **Explicit COM Threading Model Support:** See a detailed discussion of [COM Apartments in JACOB](JacobThreading.html)
  * **New COM Object Lifetime model:** See a detailed discussion of [COM Object Lifetime in JACOB](JacobComLifetime.html).
  * **Improved Event Handling:** Thanks to code contributed by [Niels Olof Bouvin](mailto:n.o.bouvin@daimi.au.dk) and [Henning Jae](mailto:jehoej@daimi.au.dk) JACOB 1.7 can read the type information of a Connection Point interface by looking it up in the registry. This makes it possible to use events with IE as well as office products.
  * **Improved Dispatch:** Error messages from Invoke failures are now printed out as well as allowing the passing in of arguments to a Get method.
  * **EnumVariant Implementation:** Makes it easier to iterate over COM collections. Thanks to code contributed by [Thomas Hallgren](mailto:Thomas.Hallgren@eoncompany.com).
  * **SafeArray leaks:** SafeArrays were not being properly freed prior to version 1.7, many other memory leaks were fixed as well.
  * **Visual Studio Project:** For those who want to debug: vstudio/JACOB. At the moment all the native code is replicated there from the jni directory...

    ## Related Links

  * The best way to get support or the latest version of JACOB is on [http://sourceforge.net/projects/jacob-project](http://sourceforge.net/projects/jacob-project) **This is the preferred way to get support for JACOB**. It also includes an extensive archive. If you are doing any development with JACOB, please join sourceforge.
  * Massimiliano Bigatti has developed [JACOBgen - a generator that automatically creates JACOB code from Type Libraries, now available on sourceforge](http://sourceforge.net/projects/jacob-project)
  * Steven Lewis is developing a version of Java2Com that supports JACOB code generation. See: [http://www.lordjoe.com/Java2Com/index.html](http://www.lordjoe.com/Java2Com/index.html).
  * To find documentation on the com.ms.com package, go to: [http://www.microsoft.com/java/download/dl_sdk40.htm](http://www.microsoft.com/java/download/dl_sdk40.htm) and at the bottom of the page is a link that says: Microsoft SDK for Java 4.0 Documentation Only. You should download that file and install it. Then, view sdkdocs.chm and look for "Microsoft Packages Reference". Hopefully, the next release of JACOB will include full javadoc (volunteers?)...