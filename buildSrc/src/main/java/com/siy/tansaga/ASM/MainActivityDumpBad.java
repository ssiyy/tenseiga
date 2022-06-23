package com.siy.tansaga.ASM;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainActivityDumpBad implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "com/siy/tansaga/MainActivity", null, "androidx/appcompat/app/AppCompatActivity", null);

        classWriter.visitInnerClass("com/siy/tansaga/MainActivity$3", null, null, 0);

        classWriter.visitInnerClass("com/siy/tansaga/MainActivity$2", null, null, 0);

        classWriter.visitInnerClass("com/siy/tansaga/MainActivity$1", null, null, 0);

        classWriter.visitInnerClass("com/siy/tansaga/R$layout", "com/siy/tansaga/R", "layout", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        classWriter.visitInnerClass("com/siy/tansaga/R$id", "com/siy/tansaga/R", "id", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        classWriter.visitInnerClass("com/siy/tansaga/R$string", "com/siy/tansaga/R", "string", ACC_PUBLIC | ACC_FINAL | ACC_STATIC);

        classWriter.visitInnerClass("android/view/View$OnClickListener", "android/view/View", "OnClickListener", ACC_PUBLIC | ACC_STATIC | ACC_ABSTRACT | ACC_INTERFACE);

        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PROTECTED, "onCreate", "(Landroid/os/Bundle;)V", null, null);
            methodVisitor.visitParameter("savedInstanceState", 0);
            methodVisitor.visitAnnotableParameterCount(1, false);
            {
                annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Landroidx/annotation/Nullable;", false);
                annotationVisitor0.visitEnd();
            }
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "androidx/appcompat/app/AppCompatActivity", "onCreate", "(Landroid/os/Bundle;)V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitLdcInsn(new Integer(2131427356));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tansaga/MainActivity", "setContentView", "(I)V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitLdcInsn(new Integer(2131231038));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tansaga/MainActivity", "findViewById", "(I)Landroid/view/View;", false);
            methodVisitor.visitTypeInsn(NEW, "com/siy/tansaga/MainActivity$1");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/siy/tansaga/MainActivity$1", "<init>", "(Lcom/siy/tansaga/MainActivity;)V", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setOnClickListener", "(Landroid/view/View$OnClickListener;)V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitLdcInsn(new Integer(2131231033));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tansaga/MainActivity", "findViewById", "(I)Landroid/view/View;", false);
            methodVisitor.visitTypeInsn(NEW, "com/siy/tansaga/MainActivity$2");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/siy/tansaga/MainActivity$2", "<init>", "(Lcom/siy/tansaga/MainActivity;)V", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setOnClickListener", "(Landroid/view/View$OnClickListener;)V", false);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitLdcInsn(new Integer(2131231034));
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tansaga/MainActivity", "findViewById", "(I)Landroid/view/View;", false);
            methodVisitor.visitTypeInsn(NEW, "com/siy/tansaga/MainActivity$3");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/siy/tansaga/MainActivity$3", "<init>", "(Lcom/siy/tansaga/MainActivity;)V", false);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "android/view/View", "setOnClickListener", "(Landroid/view/View$OnClickListener;)V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(4, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "proxySys", "()V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitLdcInsn("siy");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/siy/tansaga/MainActivity", "getBaseContext", "()Landroid/content/Context;", false);
            methodVisitor.visitLdcInsn(new Integer(2131689501));
            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tansaga/MainActivity", "com_siy_tansaga_test_HookJava_hookProxySys", "(Landroid/content/Context;I)Ljava/lang/String;", false);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC | ACC_SYNTHETIC, "access$000", "(Lcom/siy/tansaga/MainActivity;)V", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/siy/tansaga/MainActivity", "proxySys", "()V", false);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE | ACC_STATIC, "com_siy_tansaga_test_HookJava_hookProxySys", "(Landroid/content/Context;I)Ljava/lang/String;", null, null);
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ILOAD, 1);
            methodVisitor.visitIntInsn(BIPUSH, 6);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Object");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitLdcInsn(new Integer(2131689594));
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitLdcInsn("abc");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitTypeInsn(NEW, "com/siy/tansaga/test/OriginJava");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/siy/tansaga/test/OriginJava", "<init>", "()V", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_3);
            methodVisitor.visitIntInsn(BIPUSH, 6);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_4);
            methodVisitor.visitTypeInsn(NEW, "android/view/View");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitFieldInsn(GETSTATIC, "com/siy/tansaga/App", "INSTANCE", "Landroid/app/Application;");
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "android/view/View", "<init>", "(Landroid/content/Context;)V", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_5);
            methodVisitor.visitIntInsn(BIPUSH, 33);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitVarInsn(ASTORE, 2);
            methodVisitor.visitVarInsn(ALOAD, 2);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitInsn(AALOAD);
            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            methodVisitor.visitVarInsn(ISTORE, 1);
            methodVisitor.visitLdcInsn("");
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(8, 3);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
