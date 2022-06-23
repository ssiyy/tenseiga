package com.siy.tansaga.ASM;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MainActivityDump implements Opcodes {

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
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}