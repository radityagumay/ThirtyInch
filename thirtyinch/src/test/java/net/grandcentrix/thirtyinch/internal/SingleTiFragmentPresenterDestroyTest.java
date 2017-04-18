/*
 * Copyright (C) 2017 grandcentrix GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.grandcentrix.thirtyinch.internal;

import net.grandcentrix.thirtyinch.TiConfiguration;

import org.junit.Test;

import android.view.LayoutInflater;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class SingleTiFragmentPresenterDestroyTest extends TiFragmentPresenterDestroyTest {

    /**
     * Activity changing configuration without retain
     */
    @Test
    public void activityChangingConfiguration_retainFalse() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will *not* be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // And when the Activity is changing its configuration.
        hostingActivity.setChangingConfiguration(true);
        fragment.onSaveInstanceState(mSavedState);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then assert that the presenter is destroyed and not saved in the savior.
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When the Activity is recreated.
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And generates a new Fragment instance.
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());
        final TestTiFragment fragment2 = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        // And the fragment will be resumed
        fragment2.setAdded(true);
        fragment2.onCreate(mSavedState);
        fragment2.onCreateView(mock(LayoutInflater.class), null, mSavedState);
        fragment2.onStart();

        // Then a new Presenter instance will be generated and the old presenter isn't used
        assertThat(fragment2.getPresenter()).isNotEqualTo(presenter).isEqualTo(presenter2);
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * Activity changing configuration
     * Default config
     */
    @Test
    public void activityChangingConfiguration_retainTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Check that the default config matches this test case
        final TiConfiguration config = new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build();
        assertThat(TiConfiguration.DEFAULT).isEqualTo(config);

        // Given a Presenter that uses a static savior to retain itself (default config).
        final TestPresenter presenter = new TestPresenter(config);

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is changing its configurations.
        hostingActivity.setChangingConfiguration(true);
        fragment.onSaveInstanceState(mSavedState);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then the presenter will be retained and saved in the savior.
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // When the Activity is recreated.
        final HostingActivity hostingActivity2 = new HostingActivity();

        // And generates a new Fragment instance.
        final TestPresenter presenter2 = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());
        final TestTiFragment fragment2 = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity2)
                .setSavior(mSavior)
                .setPresenter(presenter2)
                .build();

        // And the fragment will be resumed
        fragment2.setAdded(true);
        fragment2.onCreate(mSavedState);
        fragment2.onCreateView(mock(LayoutInflater.class), null, mSavedState);
        fragment2.onStart();

        // Then the Presenter is the same
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(fragment.getPresenter()).isEqualTo(presenter);
        assertThat(mSavior.presenterCount()).isEqualTo(1);
    }

    /**
     * Activity finishing without retain
     */
    @Test
    public void activityFinishing_retainFalse() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter without retain.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will *not* be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then assert that the Presenter is destroyed and not saved in the savior.
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * Activity finish
     * Default config
     */
    @Test
    public void activityFinishing_retainTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Check that the default config matches this test case
        final TiConfiguration config = new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build();
        assertThat(TiConfiguration.DEFAULT).isEqualTo(config);

        // Given a Presenter that uses a static savior to retain itself (default config).
        final TestPresenter presenter = new TestPresenter(config);

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is finishing.
        hostingActivity.setFinishing(true);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then the presenter is destroyed and not saved in the savior.
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * Activity move to background -> move to foreground without retain
     */
    @Test
    public void moveToBackground_moveToForeground_retainFalse() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter that does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the Presenter will *not* be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // And when the Activity is moved to background
        fragment.onSaveInstanceState(mSavedState);
        fragment.onStop();

        // Then the Presenter is not destroyed
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();

        // And when the Activity moves to foreground again
        fragment.onStart();

        // Then the Presenter is still alive and not saved in savior
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * Activity move to background -> move to foreground
     * Default config
     */
    @Test
    public void moveToBackground_moveToForeground_retainTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Check that the default config matches this test case
        final TiConfiguration config = new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build();
        assertThat(TiConfiguration.DEFAULT).isEqualTo(config);

        // Given a Presenter that uses a static savior to retain itself (default config).
        final TestPresenter presenter = new TestPresenter(config);

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And when the Activity is moved to background
        fragment.onSaveInstanceState(mSavedState);
        fragment.onStop();

        // Then the presenter stays alive and is saved in the savior.
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // And the Fragment gets automatically retained.
        fragment.onStart();

        // Then the Presenter is the same
        assertThat(fragment.getPresenter()).isEqualTo(presenter);
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);
    }

    /**
     * removed the added fragment from the Activity without retain
     */
    @Test
    public void remove_fragment_retainFalse() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will not be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When the fragment will be removed
        fragment.setRemoving(true);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then the presenter is destroyed and not saved
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * add fragment (with backstack) press back button to pop the back stack and the remove it
     * without retain
     */
    @Test
    public void remove_fragment_retainFalse_backstackTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(false)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.setInBackstack(true);
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will not be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When the back button is pressed and the fragment will be removed
        fragment.setRemoving(true);
        fragment.onStop();
        fragment.onDestroyView();

        // Then the presenter is destroyed and not saved
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(0);

        // When the back button is pressed again the fragment will be removed from the backstack
        // and destroyed
        fragment.setInBackstack(false);
        fragment.onDestroy();

        // Then the presenter is destroyed and not saved
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }


    /**
     * removed the added fragment from the Activity
     * Default config
     */
    @Test
    public void remove_fragment_retainTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // When the fragment will be removed
        fragment.setRemoving(true);
        fragment.onStop();
        fragment.onDestroyView();
        fragment.onDestroy();

        // Then the presenter is destroyed and not saved
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }

    /**
     * add fragment (with backstack) press back button to pop the back stack and the remove it
     * Default config
     */
    @Test
    public void remove_fragment_retainTrue_backstackTrue() {

        final HostingActivity hostingActivity = new HostingActivity();

        // Given a Presenter does not retain itself.
        final TestPresenter presenter = new TestPresenter(new TiConfiguration.Builder()
                .setUseStaticSaviorToRetain(true)
                .setRetainPresenterEnabled(true)
                .build());

        // And given a Fragment.
        final TestTiFragment fragment = new TestTiFragment.Builder()
                .setDontKeepActivitiesEnabled(false)
                .setHostingActivity(hostingActivity)
                .setSavior(mSavior)
                .setPresenter(presenter)
                .build();

        // When the Fragment is added to the Activity.
        fragment.setInBackstack(true);
        fragment.onCreate(null);
        fragment.setAdded(true);
        fragment.onCreateView(mock(LayoutInflater.class), null, null);
        fragment.onStart();

        // Then the presenter will be stored in the savior
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // When the back button is pressed and the fragment will be removed
        fragment.setRemoving(true);
        fragment.onStop();
        fragment.onDestroyView();

        // Then the presenter kept alive as long as the fragment is managed by the FragmentManager
        assertThat(fragment.getPresenter().isDestroyed()).isFalse();
        assertThat(mSavior.presenterCount()).isEqualTo(1);

        // When the back button is pressed again the fragment will be removed from the backstack
        // and destroyed
        fragment.setInBackstack(false);
        fragment.onDestroy();

        // Then the presenter is destroyed and not saved
        assertThat(fragment.getPresenter().isDestroyed()).isTrue();
        assertThat(mSavior.presenterCount()).isEqualTo(0);
    }
}
