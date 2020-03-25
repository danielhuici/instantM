package info.androidhive.loginandregistration.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import info.androidhive.loginandregistration.R;
import info.androidhive.loginandregistration.controller.SQLiteHandler;
import info.androidhive.loginandregistration.controller.SessionManager;

public class ChatsActivity extends AppCompatActivity implements ActionBar.TabListener, ViewPager.OnPageChangeListener{
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private  static final String CREATE_GROUP = "Crear grupo";
    private  static final String LOGOUT = "Salir de sesión";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static final int MENU_CREATE_GROUP_ID = Menu.FIRST;
    private static final int MENU_LOGOUT_ID = Menu.FIRST + 1;
    private static final int MENU_ADD_CONTACT_ID = Menu.FIRST + 2;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private SQLiteHandler db;
    private SessionManager session;

    private ProgressDialog pDialog;
    private static final String TAG = "CHATS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = new SQLiteHandler(getApplicationContext());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        session = new SessionManager(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);

        menu.add(Menu.NONE,  MENU_CREATE_GROUP_ID, MENU_CREATE_GROUP_ID,"Crear grupo");
        menu.add(Menu.NONE, MENU_LOGOUT_ID, MENU_LOGOUT_ID, R.string.menu_item_logout);
        menu.add(Menu.NONE, MENU_ADD_CONTACT_ID, MENU_ADD_CONTACT_ID, R.string.menu_item_anadir_contacto);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
            case MENU_CREATE_GROUP_ID:
                intent = new Intent(this, EditGroupActivity.class);
                startActivity(intent);

                return true;

            case MENU_LOGOUT_ID:
                logoutUser();
                return true;

            case MENU_ADD_CONTACT_ID:
                intent = new Intent(this, AddContactActivity.class);
                startActivityForResult(intent, 2);//(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            //fragmentCommunicator.passDataToFragment("someString");
        }
    }
    // Métodos de la interfaz ActionBar.TabListener
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }

    // Métodos de la interfaz ViewPager.OnPageChangeListener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) {
        getSupportActionBar().setSelectedNavigationItem(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

/**
 * A placeholder fragment containing a simple view.

 public static class PlaceholderFragment extends Fragment {
 /**
 * The fragment argument representing the section number for this
 * fragment.

 private static final String ARG_SECTION_NUMBER = "section_number";

 public PlaceholderFragment() {
 }

 /**
 * Returns a new instance of this fragment for the given section
 * number.
 */
/**
 public static PlaceholderFragment newInstance(int sectionNumber) {
 PlaceholderFragment fragment = new PlaceholderFragment();
 Bundle args = new Bundle();
 args.putInt(ARG_SECTION_NUMBER, sectionNumber);
 fragment.setArguments(args);
 return fragment;
 }

 }
 */

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment tabFragment = null;

        switch (position){
            case 0:
                tabFragment = new TabAFragment();
                break;
            case 1:
                tabFragment = new TabBFragment();
                break;
        }
        return tabFragment;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String section = null;

        switch (position) {
            case 0:
                section = getString(R.string.chats);
                break;
            case 1:
                section =  getString(R.string.contacts);
                break;
        }
        return section;
    }
}

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();
        db.deleteGroups();

        // Lanzar actividad de login
        Intent intent = new Intent(ChatsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
