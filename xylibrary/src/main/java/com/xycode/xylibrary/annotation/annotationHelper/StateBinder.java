package com.xycode.xylibrary.annotation.annotationHelper;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;

import com.xycode.xylibrary.annotation.SaveState;
import com.xycode.xylibrary.utils.L;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/8.
 */
public class StateBinder {
    public static void saveState(Object target, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        if (target == null) {
            return;
        }
        Class targetClass = target.getClass();
        Field[] fields = targetClass.getFields();
        if (fields != null) {
            for (Field field : fields) {
                if (field != null) {
                    SaveState statue = field.getAnnotation(SaveState.class);
                    if (statue != null) {
                        try {
                            Object data = field.get(target);
                            Class fieldClazz = field.getType();
                            if (data != null) {
                                if (fieldClazz.isPrimitive()) {
                                    if (data.getClass() == Byte.class) {
                                        L.d(field.getName() + " is byte");
                                        bundle.putByte(field.getName(), (Byte) data);
                                    }
                                    if (data.getClass() == Short.class) {
                                        L.d(field.getName() + " is short");
                                        bundle.putShort(field.getName(), (Short) data);
                                    }
                                    if (data.getClass() == Integer.class) {
                                        L.d(field.getName() + " is int");
                                        bundle.putInt(field.getName(), (Integer) data);
                                    }
                                    if (data.getClass() == Long.class) {
                                        L.d(field.getName() + " is long");
                                        bundle.putLong(field.getName(), (Long) data);
                                    }
                                    if (data.getClass() == Float.class) {
                                        L.d(field.getName() + " is float");
                                        bundle.putFloat(field.getName(), (Float) data);
                                    }
                                    if (data.getClass() == Double.class) {
                                        L.d(field.getName() + " is double");
                                        bundle.putDouble(field.getName(), (Double) data);
                                    }

                                    if (data.getClass() == Character.class) {
                                        L.d(field.getName() + " is char");
                                        bundle.putChar(field.getName(), (Character) data);
                                    }
                                    if (data.getClass() == Boolean.class) {
                                        L.d(field.getName() + " is boolean");
                                        bundle.putBoolean(field.getName(), (Boolean) data);
                                    }
                                } else {

                                    //String
                                    if (data.getClass() == String.class) {
                                        L.d(field.getName() + " is String");
                                        bundle.putString(field.getName(), (String) data);
                                    } else if (!fieldClazz.isArray() && isImplementTarget(fieldClazz, Serializable.class)) {
                                        L.d(field.getName() + " is Serializable");
                                        bundle.putSerializable(field.getName(), (Serializable) data);
                                    } else if (!fieldClazz.isArray() && isImplementTarget(fieldClazz, Parcelable.class)) {
                                        L.d(field.getName() + " is Parcelable");
                                        bundle.putParcelable(field.getName(), (Parcelable) data);
                                    } else if (data.getClass() == List.class) {
                                        Type fc = field.getGenericType();
                                        if (fc != null) {
                                            if (fc instanceof ParameterizedType) {
                                                ParameterizedType pt = (ParameterizedType) fc;
                                                Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                                                if (isImplementTarget(genericClazz, Parcelable.class)) {
                                                    L.d(field.getName() + " is ParcelableList");
                                                    bundle.putParcelableArrayList(field.getName(), (ArrayList<? extends Parcelable>) data);
                                                }
                                            }
                                        }
                                    } else if (fieldClazz.isArray()) {
                                        if (isImplementTarget(fieldClazz.getComponentType(), Parcelable.class))
                                            L.d(field.getName() + " is Parcelable array");
                                        bundle.putParcelableArray(field.getName(), (Parcelable[]) data);
                                    } else if (fieldClazz == SparseArray.class) {
                                        boolean parcelable = false;
                                        if (data != null) {
                                            for (int i = 0; i < ((SparseArray) data).size(); i++) {
                                                if (((SparseArray) data).get(i) != null) {
                                                    if (isImplementTarget(((SparseArray) data).get(i).getClass(), Parcelable.class)) {
                                                        parcelable = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (parcelable) {
                                            L.d(field.getName() + " is Parcelable sparseArray");
                                            bundle.putSparseParcelableArray(field.getName(), (SparseArray<? extends Parcelable>) data);
                                        }
                                    } else {
                                        L.e(field.getName() + "'s java type is unsupported , you can only use the @SaveState at a field that its java type is supported by Bundle");
                                    }
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    throw new NullPointerException("not support @");
                }


            }
        }


        Field[] privateFields = targetClass.getDeclaredFields();
        if (privateFields != null) {
            for (Field field : privateFields) {
                if (field != null) {
                    field.setAccessible(true);
                    SaveState statue = field.getAnnotation(SaveState.class);
                    if (statue != null) {
                        try {
                            Object data = field.get(target);
                            if(data!=null){
                                Class fieldClazz = field.getType();
                                if (fieldClazz.isPrimitive()) {
                                    if (data.getClass() == Byte.class) {
                                        L.d(field.getName() + " is byte");
                                        bundle.putByte(field.getName(), (Byte) data);
                                    }
                                    if (data.getClass() == Short.class) {
                                        L.d(field.getName() + " is short");
                                        bundle.putShort(field.getName(), (Short) data);
                                    }
                                    if (data.getClass() == Integer.class) {
                                        L.d(field.getName() + " is int");
                                        bundle.putInt(field.getName(), (Integer) data);
                                    }
                                    if (data.getClass() == Long.class) {
                                        L.d(field.getName() + " is long");
                                        bundle.putLong(field.getName(), (Long) data);
                                    }
                                    if (data.getClass() == Float.class) {
                                        L.d(field.getName() + " is float");
                                        bundle.putFloat(field.getName(), (Float) data);
                                    }
                                    if (data.getClass() == Double.class) {
                                        L.d(field.getName() + " is double");
                                        bundle.putDouble(field.getName(), (Double) data);
                                    }

                                    if (data.getClass() == Character.class) {
                                        L.d(field.getName() + " is char");
                                        bundle.putChar(field.getName(), (Character) data);
                                    }
                                    if (data.getClass() == Boolean.class) {
                                        L.d(field.getName() + " is boolean");
                                        bundle.putBoolean(field.getName(), (Boolean) data);
                                    }
                                } else {
                                    //String
                                    if (data.getClass() == String.class) {
                                        L.d(field.getName() + " is String");
                                        bundle.putString(field.getName(), (String) data);
                                    } else if (!fieldClazz.isArray() && isImplementTarget(fieldClazz, Serializable.class)) {
                                        L.d(field.getName() + " is Serializable");
                                        bundle.putSerializable(field.getName(), (Serializable) data);
                                    } else if (!fieldClazz.isArray() && isImplementTarget(fieldClazz, Parcelable.class)) {
                                        L.d(field.getName() + " is Parcelable");
                                        bundle.putParcelable(field.getName(), (Parcelable) data);
                                    } else if (data.getClass() == List.class) {
                                        Type fc = field.getGenericType();
                                        if (fc != null) {
                                            if (fc instanceof ParameterizedType) {
                                                ParameterizedType pt = (ParameterizedType) fc;
                                                Class genericClazz = (Class) pt.getActualTypeArguments()[0];
                                                if (isImplementTarget(genericClazz, Parcelable.class)) {
                                                    L.d(field.getName() + " is ParcelableList");
                                                    bundle.putParcelableArrayList(field.getName(), (ArrayList<? extends Parcelable>) data);
                                                }
                                            }
                                        }
                                    } else if (fieldClazz.isArray()) {
                                        if (isImplementTarget(fieldClazz.getComponentType(), Parcelable.class))
                                            L.d(field.getName() + " is Parcelable array");
                                        bundle.putParcelableArray(field.getName(), (Parcelable[]) data);
                                    } else if (fieldClazz == SparseArray.class) {
                                        boolean parcelable = false;
                                        if (data != null) {
                                            for (int i = 0; i < ((SparseArray) data).size(); i++) {
                                                if (((SparseArray) data).get(i) != null) {
                                                    if (isImplementTarget(((SparseArray) data).get(i).getClass(), Parcelable.class)) {
                                                        parcelable = true;
                                                    }
                                                }
                                            }
                                        }
                                        if (parcelable) {
                                            L.d(field.getName() + " is Parcelable sparseArray");
                                            bundle.putSparseParcelableArray(field.getName(), (SparseArray<? extends Parcelable>) data);
                                        }
                                    } else {
                                        L.e(field.getName() + "'s java type is unsupported, you can only use the @SaveState at a field that its java type is supported by Bundle");
                                    }
                                }
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    field.setAccessible(false);
                } else {
                    throw new NullPointerException("not support @");
                }
            }
        }
    }


    public static void bindState(Object target, Bundle source) {
        if (source == null) {
            return;
        }
        if (target == null) {
            return;
        }
        Class targetClass = target.getClass();
        Field[] fields = targetClass.getFields();
        if (fields != null) {
            for (Field field : fields) {
                SaveState statue = field.getAnnotation(SaveState.class);
                if (statue != null) {
                    Object data = source.get(field.getName());
                    try {
                        field.set(target, data);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Field[] privateFields = targetClass.getFields();
        if (fields != null) {
            for (Field field : privateFields) {
                field.setAccessible(true);
                SaveState statue = field.getAnnotation(SaveState.class);
                if (statue != null) {
                    Object data = source.get(field.getName());
                    try {
                        field.set(target, data);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } finally {
                        field.setAccessible(false);
                    }
                }
            }
        }
    }

    public static boolean isImplementTarget(Class clz, Class target) {
        boolean flag = false;
        Class[] temp = clz.getInterfaces();
        if (temp != null) {
            for (Class clzs : temp) {
                if (clzs == target) {
                    flag = true;
                }
            }
        }
        return flag;
    }
}
