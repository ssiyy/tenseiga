package com.siy.tenseiga.log;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class OriginJavaDump implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/siy/tenseiga/test/OriginJava", null, "java/lang/Object", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE, "newField", "Ljava/lang/Object;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "replace", "(II)I", null, null);
            methodVisitor.visitParameter("a", 0);
            methodVisitor.visitParameter("b", 0);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/test/OriginJava", "com_siy_tenseiga_test_HookJava_hookReplace", "(Lcom/siy/tenseiga/test/OriginJava;II)I", false);
            methodVisitor.visitInsn(IRETURN);
            methodVisitor.visitMaxs(3, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "proxy", "(ILjava/lang/Integer;Ljava/lang/String;Landroid/view/View;Landroid/content/Context;BS)I", null, null);
            methodVisitor.visitParameter("a", 0);
            methodVisitor.visitParameter("b", 0);
            methodVisitor.visitParameter("str", 0);
            methodVisitor.visitParameter("view", 0);
            methodVisitor.visitParameter("context", 0);
            methodVisitor.visitParameter("bb", 0);
            methodVisitor.visitParameter("sh", 0);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            methodVisitor.visitLdcInsn("OriginJava-proxy-");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 4);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ALOAD, 5);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("byte:");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ILOAD, 6);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("short:");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitVarInsn(ILOAD, 7);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitInsn(IADD);
            methodVisitor.visitInsn(IRETURN);
            methodVisitor.visitMaxs(3, 8);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "showToast", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitFieldInsn(GETSTATIC, "com/siy/tenseiga/App", "INSTANCE", "Landroid/app/Application;");
            methodVisitor.visitLdcInsn("OriginJava-showToast");
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/widget/Toast", "makeText", "(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/widget/Toast", "show", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "annoReplace", "(ILjava/lang/Integer;)Ljava/lang/String;", null, null);
            methodVisitor.visitParameter("a", 0);
            methodVisitor.visitParameter("b", 0);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/test/OriginJava", "com_siy_tenseiga_test_HookJava_hookAnnoReplace", "(Lcom/siy/tenseiga/test/OriginJava;ILjava/lang/Integer;)Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(3, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "replace$___backup___", "(II)I", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitLdcInsn("OriginJava-replace-");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitInsn(IADD);
            methodVisitor.visitInsn(IRETURN);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "com_siy_tenseiga_test_HookJava_hookReplace", "(Lcom/siy/tenseiga/test/OriginJava;II)I", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitLdcInsn("HookJava-hookReplace-");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tenseiga/test/OriginJava", "showToast", "()V", false);
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Number");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Number");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tenseiga/test/OriginJava", "replace$___backup___", "(II)I", false);
            methodVisitor.visitVarInsn(ISTORE, 4);
            methodVisitor.visitVarInsn(ILOAD, 4);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitInsn(ISUB);
            methodVisitor.visitInsn(IRETURN);
            methodVisitor.visitMaxs(4, 5);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "annoReplace$___backup___", "(ILjava/lang/Integer;)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitLdcInsn("OriginJava-annoReplace-");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitInsn(IADD);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(2, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "com_siy_tenseiga_test_HookJava_hookAnnoReplace", "(Lcom/siy/tenseiga/test/OriginJava;ILjava/lang/Integer;)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitLdcInsn("OriginJava-hookAnnoReplace-");
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tenseiga/test/OriginJava", "showToast", "()V", false);
            methodVisitor.visitIntInsn(BIPUSH, 7);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitInsn(SWAP);
            methodVisitor.visitFieldInsn(PUTFIELD, "com/siy/tenseiga/test/OriginJava", "newField", "Ljava/lang/Object;");
            methodVisitor.visitTypeInsn(NEW, "java/lang/StringBuilder");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitInsn(IADD);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitLdcInsn("hook");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(3, 4);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
