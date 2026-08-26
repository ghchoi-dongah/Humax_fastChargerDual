package com.dongah.fastcharger.websocket.ocpp.utilities;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

public final class MoreObjects {

    /**
     * Utility classes should have no instances.
     */
    private MoreObjects() {
        throw new AssertionError("No Objects instance should exi");
    }


    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && Objects.equals(a, b));
    }

    public static boolean deepEquals(Object a, Object b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else {
            return Arrays.deepEquals(new Object[]{a}, new Object[]{b});
        }
    }

    public static int hasCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }


    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    public static <T> T[] clone(T[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

    public static byte[] clone(byte[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

    public static ToStringHelper toStringHelper(Object self) {
        return new ToStringHelper(self);
    }


    public static final class ToStringHelper {
        public static final int MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS = 32;
        public static final String FIELD_NAME_LENGTH_POSTFIX = ".length";
        public static final String FIELD_NAME_SIZE_POSTFIX = ".size";
        public static final String SECURE_FIELD_VALUE_REPLACEMENT = "********";

        private final boolean outputFullDetails;
        private final ToStringHelperImpl helperImplementation;


        public ToStringHelper(ToStringHelperImpl helperImplementation, boolean outputFullDetails) {
            this.outputFullDetails = outputFullDetails;
            this.helperImplementation = helperImplementation;
        }

        private ToStringHelper(Object self) {
            this(toStringHelper(self), false);
        }

        private ToStringHelper(Class<?> clazz) {
            this(toStringHelper(clazz), false);
        }

        private ToStringHelper(String className) {
            this(toStringHelper(className), false);
        }

        private ToStringHelper(Object self, boolean outputFullDetails) {
            this(toStringHelper(self), outputFullDetails);
        }

        private ToStringHelper(Class<?> clazz, boolean outputFullDetails) {
            this(toStringHelper(clazz), outputFullDetails);
        }

        private ToStringHelper(String className, boolean outputFullDetails) {
            this(toStringHelper(className), outputFullDetails);
        }

        /***
         * Exclude from output fields with null value
         * @return ToStringHelper instance
         */
        public ToStringHelper omitNullValues() {
            helperImplementation.omitNullValues();
            return this;
        }

        /***
         * Add field name add value to output. It's safe to pass null as value
         * @param name filed name
         * @param value filed value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, Object value) {
            helperImplementation.add(name, value);
            return this;
        }

        /***
         * Add field name and value to output. It's safe to pass null as value.
         * @param name filed name
         * @param value filed value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, ZonedDateTime value) {
            helperImplementation.add(name, value);
            return this;
        }

        /**
         * Add filed name and value to output. It's safe to pass null as value
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, boolean value) {
            helperImplementation.add(name, value);
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, char value) {
            helperImplementation.add(name, String.valueOf(value));
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, double value) {
            helperImplementation.add(name, String.valueOf(value));
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, float value) {
            helperImplementation.add(name, String.valueOf(value));
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, int value) {
            helperImplementation.add(name, String.valueOf(value));
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, long value) {
            helperImplementation.add(name, String.valueOf(value));
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, List<?> value) {
            return addCollection(name, value);
        }

        /**
         * Add field name and value to output. It's safe to pass null as value
         *
         * @param name
         * @param value
         * @return
         */
        public ToStringHelper add(String name, Set<?> value) {
            return addCollection(name, value);
        }

        /***
         * Add filed name and value to output. It's safe to pass null as value.
         * @param name
         * @param value
         * @return
         */
        public ToStringHelper add(String name, Map<?, ?> value) {
            return addMap(name, value);
        }

        /***
         * Add filed name and value to output. It's safe to pass null as value.
         * @param name
         * @param value
         * @return
         */
        public ToStringHelper add(String name, Queue<?> value) {
            return addCollection(name, value);
        }

        private ToStringHelper addCollection(String name, Collection<?> value) {
            if (value != null && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_SIZE_POSTFIX, value.size());
            } else {
                helperImplementation.add(name, value);
            }
            return this;
        }

        private ToStringHelper addMap(String name, Map<?, ?> value) {
            if (value != null && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_SIZE_POSTFIX, value.size());
            } else {
                helperImplementation.add(name, value);
            }
            return this;
        }

        /***
         * Add filed name and value to output. It's safe to pass null as value.
         * @param name
         * @param value
         * @param <T>
         * @return
         */
        public <T> ToStringHelper add(String name, T[] value) {
            if (value != null && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add filed name and value to output. It's safe to pass null as value.
         *
         * @param name  filed name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, byte[] value) {
            if (value != null && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, boolean[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, char[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, double[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, float[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, int[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper add(String name, long[] value) {
            if (value != null
                    && value.length > MAXIMUM_ARRAY_SIZE_TO_OUTPUT_DETAILS
                    && !outputFullDetails) {
                helperImplementation.add(name + FIELD_NAME_LENGTH_POSTFIX, value.length);
            } else {
                helperImplementation.add(name, Arrays.toString(value));
            }
            return this;
        }

        /**
         * Add field name and mask instead of real value to output. It's safe to pass null as value.
         *
         * @param name  field name
         * @param value field value
         * @return ToStringHelper instance
         */
        public ToStringHelper addSecure(String name, String value) {
            value = SECURE_FIELD_VALUE_REPLACEMENT;
            helperImplementation.add(name, value);
            return this;
        }

        /***
         * Add value to output
         * @param value
         * @return
         */
        public ToStringHelper addValue(Object value) {
            helperImplementation.addValue(value);
            return this;
        }

        /**
         * Add Value to output
         *
         * @param value to add  to output
         * @return ToStringHelper instance
         */
        public ToStringHelper addValue(boolean value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        public ToStringHelper addValue(char value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        public ToStringHelper addValue(double value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        public ToStringHelper addValue(float value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        public ToStringHelper addValue(int value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        public ToStringHelper addValue(long value) {
            helperImplementation.addValue(String.valueOf(value));
            return this;
        }

        /**
         * Return resulting output string
         *
         * @return resulting output string
         */
        public String toString() {
            return helperImplementation.toString();
        }

        /***
         * Creates an instance of {@link ToStringHelperImpl}
         * @param self the object to generate the string for (typically {@code this}, used only for its
         *             class name
         * @return
         */
        static ToStringHelperImpl toStringHelper(Object self) {
            return new ToStringHelperImpl(self.getClass().getSimpleName());
        }

        /**
         * Creates an instance of {@link ToStringHelperImpl} in the same manner as {@link
         * #toStringHelper(Object)}, but using the simple name if {@code clazz} instead of using an
         * instance's {@link Object#getClass()}.
         *
         * @param clazz {@link Class} of the instance
         * @return
         */
        static ToStringHelperImpl toStringHelper(Class<?> clazz) {
            return new ToStringHelperImpl(clazz.getSimpleName());
        }

        /**
         * Creates an instance of {@link ToStringHelperImpl} in the same manner ad {@link
         * #ToStringHelper(Object)}, but using {@code classname} instead of using ab instance's {@link
         * Object#getClass()}.
         *
         * @param className the name of the instance type
         * @return
         */
        public static ToStringHelperImpl toStringHelper(String className) {
            return new ToStringHelperImpl(className);
        }
    }

    /***
     * Support class for {@link MoreObjects#toStringHelper}
     */
    public static final class ToStringHelperImpl {
        private final String className;
        private final ValueHolder holderHead = new ValueHolder();
        private ValueHolder holderTail = holderHead;
        private boolean omitNullValues = false;

        public ToStringHelperImpl(String className) {
            this.className = className;
        }

        /**
         * Configures the {@link ToStringHelperImpl} so {@link #toString()} will ignore properties with
         * null value. The order of calling this method, relative to the {@code add()}/{@cod
         * addValue()} methods, is not significant.
         *
         * @return
         */
        public ToStringHelperImpl omitNullValues() {
            omitNullValues = true;
            return this;
        }

        /**
         * Adds a name/value pair to the formatted output in {@code name=value} format. if {@code value}
         * is {@code null}, the string {@code "null"} is used, unless {@link #omitNullValues()} is
         * called, in which case this name/value pair will not be added
         *
         * @param name
         * @param value
         * @return
         */
        public ToStringHelperImpl add(String name, Object value) {
            return addHolder(name, value);
        }

        public ToStringHelperImpl add(String name, ZonedDateTime value) {
            return addHolder(name, value);
        }

        public ToStringHelperImpl add(String name, boolean value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl add(String name, char value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl add(String name, double value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl add(String name, float value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl add(String name, int value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl add(String name, long value) {
            return addHolder(name, String.valueOf(value));
        }

        public ToStringHelperImpl addValue(Object value) {
            return addHolder(value);
        }

        public ToStringHelperImpl addValue(boolean value) {
            return addHolder(String.valueOf(value));
        }

        public ToStringHelperImpl addValue(char value) {
            return addHolder(String.valueOf(value));
        }

        public ToStringHelperImpl addValue(double value) {
            return addHolder(String.valueOf(value));
        }

        public ToStringHelperImpl addValue(float value) {
            return addHolder(String.valueOf(value));
        }

        public ToStringHelperImpl addValue(int value) {
            return addHolder(String.valueOf(value));
        }

        public ToStringHelperImpl addValue(long value) {
            return addHolder(String.valueOf(value));
        }


        @Override
        public String toString() {
            // create a copy to keep it consistent in case value changes
            boolean omitNullValuesSnapshot = omitNullValues;
            String nextSeparator = "";
            StringBuilder builder = new StringBuilder(32).append(className).append('{');

            for (ValueHolder valueHolder = holderHead.next; valueHolder != null; valueHolder = valueHolder.next) {
                Object value = valueHolder.value;
                if (!omitNullValuesSnapshot || value != null) {
                    builder.append(nextSeparator);
                    nextSeparator = ", ";
                    if (valueHolder.name != null) {
                        builder.append(valueHolder.name).append("=");
                    }
                    if (value != null && value.getClass().isArray()) {
                        Object[] objectArray = {value};
                        String arrayString = Arrays.deepToString(objectArray);
                        builder.append(arrayString, 1, arrayString.length() - 1);
                    } else {
                        builder.append(value);
                    }
                }
            }
            return builder.append('}').toString();
        }

        private ValueHolder addHolder() {
            ValueHolder valueHolder = new ValueHolder();
            holderTail = holderTail.next = valueHolder;
            return valueHolder;
        }

        private ToStringHelperImpl addHolder(Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            return this;
        }

        private ToStringHelperImpl addHolder(String name, Object value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = value;
            valueHolder.name = name;
            return this;
        }

        private ToStringHelperImpl addHolder(String name, ZonedDateTime value) {
            ValueHolder valueHolder = addHolder();
            valueHolder.value = "/" + SugarUtil.zonedDateTimeToString(value) + "\"";
            valueHolder.name = name;
            return this;
        }
    }

    private static final class ValueHolder {
        String name;
        Object value;
        ValueHolder next;
    }


}
