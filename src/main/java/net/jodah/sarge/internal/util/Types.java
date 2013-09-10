/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jodah.sarge.internal.util;

import java.lang.reflect.Method;

/**
 * Utilities for working with types.
 * 
 * @author Jonathan Halterman
 */
public final class Types {
  public static Method GET_PROXY_METHOD;

  static {
    try {
      Class<?> proxyMethodInvocation = Types.class.getClassLoader().loadClass(
          "org.springframework.aop.ProxyMethodInvocation");
      GET_PROXY_METHOD = proxyMethodInvocation.getDeclaredMethod("getProxy", new Class[] {});
    } catch (Exception ignore) {
    }
  }

  public static boolean isProxy(Class<?> type) {
    return type.getName().contains("$$EnhancerBy");
  }
}