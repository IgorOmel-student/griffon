/*
 * Copyright 2008-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.runtime.pivot;

import griffon.core.ExceptionHandler;
import org.codehaus.griffon.runtime.core.threading.AbstractUIThreadManager;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

/**
 * Executes code honoring Pivot's threading model.
 *
 * @author Andres Almiray
 * @since 2.0.0
 */
public class PivotUIThreadManager extends AbstractUIThreadManager {
    private final ExceptionHandler exceptionHandler;

    @Inject
    public PivotUIThreadManager(@Nonnull ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public boolean isUIThread() {
        return EventQueue.isDispatchThread();
    }

    @Override
    public void runInsideUIAsync(@Nonnull Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

    @Override
    public void runInsideUISync(@Nonnull final Runnable runnable) {
        if (isUIThread()) {
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (InterruptedException e) {
                exceptionHandler.uncaughtException(Thread.currentThread(), e);
            } catch (InvocationTargetException e) {
                exceptionHandler.uncaughtException(Thread.currentThread(), e.getTargetException());
            }
        }
    }
}
