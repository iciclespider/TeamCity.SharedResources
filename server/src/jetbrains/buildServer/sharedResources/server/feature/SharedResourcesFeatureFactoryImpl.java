/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.sharedResources.server.feature;

import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Oleg Rybak (oleg.rybak@jetbrains.com)
 */
public final class SharedResourcesFeatureFactoryImpl implements SharedResourcesFeatureFactory {

  @NotNull
  private final Locks myLocks;

  @NotNull
  private final Resources myResources;

  public SharedResourcesFeatureFactoryImpl(@NotNull final Locks locks, @NotNull Resources resources) {
    myLocks = locks;
    myResources = resources;
  }

  @NotNull
  @Override
  public SharedResourcesFeature createFeature(@NotNull final SBuildFeatureDescriptor descriptor) {
    return new SharedResourcesFeatureImpl(myLocks, myResources, descriptor);
  }
}
