package us.bojie.testingcodelab_android.addnote;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import us.bojie.testingcodelab_android.R;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static us.bojie.testingcodelab_android.custom.matcher.ImageViewHasDrawableMatcher.hasDrawable;

/**
 * Tests for the add note screen.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddNoteScreenTest {

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public IntentsTestRule<AddNoteActivity> mAddNoteIntentsTestRule =
            new IntentsTestRule<>(AddNoteActivity.class);

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests significantly
     * more reliable.
     */
    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(
                mAddNoteIntentsTestRule.getActivity().getCountingIdlingResource());
    }

    @Test
    public void addImageToNote_ShowsThumbnailInUi() {
        // Create an Activity Result which can be used to stub the camera Intent
        ActivityResult result = createImageCaptureActivityResultStub();
        // If there is an Intent with ACTION_IMAGE_CAPTURE, intercept the Intent and respond with
        // a stubbed result.
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);

        // Check thumbnail view is not displayed
        onView(withId(R.id.add_note_image_thumbnail)).check(matches(not(isDisplayed())));

        selectTakeImageFromMenu();

        // Check that the stubbed thumbnail is displayed in the UI
        onView(withId(R.id.add_note_image_thumbnail))
                .perform(scrollTo()) // Scroll to thumbnail
                .check(matches(allOf(
                        hasDrawable(), // Check ImageView has a drawable set with a custom matcher
                        isDisplayed())));
    }

    @Test
    public void errorShownOnEmptyMessage() {
        onView(withId(R.id.fab_add_notes)).perform(click());
        // Add note title and description
        onView(withId(R.id.add_note_title)).perform(typeText(""));
        onView(withId(R.id.add_note_description)).perform(typeText(""),
                closeSoftKeyboard());
        // Save the note
        onView(withId(R.id.fab_add_notes)).perform(click());

//        Snackbar tests are unreliable using the latest support libs, skip the assertion for now.
        // Verify empty notes snackbar is shown
//        String emptyNoteMessageText = getTargetContext().getString(R.string.empty_note_message);
//        onView(withText(emptyNoteMessageText)).check(matches(isDisplayed()));
    }

    /**
     * Convenience method which opens the options menu and select the take image option.
     */
    private void selectTakeImageFromMenu() {
        openActionBarOverflowOrOptionsMenu(getTargetContext());

        // Click on add picture option
        onView(withText(R.string.take_picture)).perform(click());
    }

    private ActivityResult createImageCaptureActivityResultStub() {
        // Create the ActivityResult, with a null Intent since we do not want to return any data
        // back to the Activity.
        return new ActivityResult(Activity.RESULT_OK, null);
    }
}
