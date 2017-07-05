/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @summary AOT methods should be swept if a super class has changed (with CDS).
 * @library /test/lib /
 * @modules java.base/jdk.internal.misc
 *          java.management
 * @requires vm.bits == "64" & os.arch == "amd64" & os.family == "linux"
 * @build compiler.aot.fingerprint.SelfChanged
 *        compiler.aot.AotCompiler
 *
 * @run main compiler.aot.fingerprint.SelfChanged WRITE-UNMODIFIED-CLASS
 * @run main/othervm compiler.aot.AotCompiler -libname libSelfChanged.so
 *      -class compiler.aot.fingerprint.Blah
 *
 * @run main ClassFileInstaller -jar SelfChangedCDS.jar compiler.aot.fingerprint.Blah
 * @run main compiler.aot.fingerprint.CDSDumper SelfChangedCDS.jar SelfChangedCDS.classlist SelfChangedCDS.jsa
 *      compiler.aot.fingerprint.Blah
 *
 * @run main compiler.aot.fingerprint.CDSRunner -cp SelfChangedCDS.jar
 *      compiler.aot.fingerprint.Blah TEST-UNMODIFIED
 * @run main compiler.aot.fingerprint.CDSRunner -cp SelfChangedCDS.jar
 *      -XX:+UseAOT -XX:+PrintAOT -XX:AOTLibrary=./libSelfChanged.so
 *      -XX:+UnlockDiagnosticVMOptions -XX:SharedArchiveFile=SelfChangedCDS.jsa
 *      -Xshare:auto -XX:+UnlockCommercialFeatures -XX:+UseAppCDS -showversion
 *      -Xlog:aot+class+fingerprint=trace -Xlog:aot+class+load=trace
 *      compiler.aot.fingerprint.Blah TEST-UNMODIFIED
 *
 * @run main
 *      compiler.aot.fingerprint.SelfChanged WRITE-MODIFIED-CLASS
 * @run main/othervm compiler.aot.AotCompiler -libname libSelfChanged.so
 *      -class compiler.aot.fingerprint.Blah
 *
 * @run main compiler.aot.fingerprint.CDSRunner -cp SelfChangedCDS.jar
 *      compiler.aot.fingerprint.Blah TEST-MODIFIED
 * @run main compiler.aot.fingerprint.CDSRunner -cp SelfChangedCDS.jar
 *      -XX:+UseAOT -XX:+PrintAOT -XX:AOTLibrary=./libSelfChanged.so
 *      -XX:+UnlockDiagnosticVMOptions -XX:SharedArchiveFile=SelfChangedCDS.jsa
 *      -Xshare:auto -XX:+UnlockCommercialFeatures -XX:+UseAppCDS -showversion
 *      -Xlog:aot+class+fingerprint=trace -Xlog:aot+class+load=trace
 *      compiler.aot.fingerprint.Blah TEST-MODIFIED
 */
