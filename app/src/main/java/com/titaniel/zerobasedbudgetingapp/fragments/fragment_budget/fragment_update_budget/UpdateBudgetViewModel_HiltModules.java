package com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap;
import dagger.hilt.codegen.OriginatingElement;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.StringKey;
import java.lang.String;

@OriginatingElement(
    topLevelClass = UpdateBudgetViewModel.class
)
public final class UpdateBudgetViewModel_HiltModules {
  private UpdateBudgetViewModel_HiltModules() {
  }

  @Module
  @InstallIn(ViewModelComponent.class)
  public abstract static class BindsModule {
    @Binds
    @IntoMap
    @StringKey("com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetViewModel")
    @HiltViewModelMap
    public abstract ViewModel binds(UpdateBudgetViewModel vm);
  }

  @Module
  @InstallIn(ActivityRetainedComponent.class)
  public static final class KeyModule {
    private KeyModule() {
    }

    @Provides
    @IntoSet
    @HiltViewModelMap.KeySet
    public static String provide() {
      return "com.titaniel.zerobasedbudgetingapp.fragments.fragment_budget.fragment_update_budget.UpdateBudgetViewModel";
    }
  }
}
