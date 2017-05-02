/*
 * Copyright 2017 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.bytecoder.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class BytecodeLinkerContext {

    private final Map<BytecodeObjectTypeRef, BytecodeLinkedClass> linkedClasses;
    private final BytecodeLoader loader;

    public BytecodeLinkerContext(BytecodeLoader aLoader) {
        linkedClasses = new HashMap<>();
        loader = aLoader;
    }

    public BytecodeLinkedClass linkClass(BytecodeObjectTypeRef aTypeRef) {
        return linkedClasses.computeIfAbsent(aTypeRef,
                new Function<BytecodeObjectTypeRef, BytecodeLinkedClass>() {
                    @Override
                    public BytecodeLinkedClass apply(BytecodeObjectTypeRef aTypeRef) {
                        try {
                            String theResourceName = "/" + aTypeRef.name().replace(".", "/") + ".class";
                            BytecodeClass theLoadedClass = loader.loadByteCode(getClass().getResourceAsStream(theResourceName));

                            return new BytecodeLinkedClass(aTypeRef, theLoadedClass);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public void linkClassMethod(BytecodeObjectTypeRef aTypeRef, String aMethodName) {
        linkClass(aTypeRef).linkMethod(aMethodName);
    }

    public void forEachClass(Consumer<Map.Entry<BytecodeObjectTypeRef, BytecodeLinkedClass>> aConsumer) {
        linkedClasses.entrySet().forEach(aConsumer);
    }
}