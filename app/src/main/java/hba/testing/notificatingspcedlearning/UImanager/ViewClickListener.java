package hba.testing.notificatingspcedlearning.UImanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.job.JobInfo;

import java.util.Date;

import hba.testing.notificatingspcedlearning.Activity.AddItem;
import hba.testing.notificatingspcedlearning.Activity.CategoryList;
import hba.testing.notificatingspcedlearning.Activity.ItemDetail;
import hba.testing.notificatingspcedlearning.Activity.MainSearch;
import hba.testing.notificatingspcedlearning.Activity.OnlineSearch;
import hba.testing.notificatingspcedlearning.DatabaseHelper.Query;
import hba.testing.notificatingspcedlearning.DatabaseHelper.dtbConst;
import hba.testing.notificatingspcedlearning.LearningStrategy.Strategy;
import hba.testing.notificatingspcedlearning.NetworkHandler.DataHandler.Entry;
import hba.testing.notificatingspcedlearning.R;
import hba.testing.notificatingspcedlearning.Service.MaintainanceService;

/**
 * Created by TranKim on 12/18/2016.
 */
public final class ViewClickListener {
    /**
     * ViewClickListener for each item on the list view of Main search
     */
    public static class ListViewItemListener implements View.OnClickListener {
        private Context ct;

        public ListViewItemListener(Context ct) {
            this.ct = ct;
        }

        @Override
        public void onClick(View v) {
            Intent openAdd = new Intent(ct, ItemDetail.class);
            openAdd.putExtra(ItemDetail.INTENT_START_ITEM_DETAIL_KEY, (int) v.getTag());
            ct.startActivity(openAdd);
        }
    }

    /**
     * ViewClickListener for each item on the list view of Main search in delete mode
     */
    public static class ListViewItemListenerDeleteMode implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CheckBox chkBox = (CheckBox) v.findViewById(R.id.main_search_delete_item_status);
            chkBox.setChecked(!chkBox.isChecked());
            ((Bundle) chkBox.getTag()).putBoolean((String) v.getTag(), chkBox.isChecked());
        }
    }

    public static class ExpandableListViewItemListener implements ExpandableListView.OnChildClickListener {
        private Context ct;

        public ExpandableListViewItemListener(Context context) {
            ct = context;
        }

        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Intent openAdd = new Intent(ct, ItemDetail.class);
            openAdd.putExtra(ItemDetail.INTENT_START_ITEM_DETAIL_KEY, (int) v.getTag());
            ct.startActivity(openAdd);
            return true;
        }
    }

    /**
     * Checkbox listener for each checkbox on the list view of Main search in delete mode
     */
    public static class ListViewCheckboxItemListenerDeleteMode implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LinearLayout parent = (LinearLayout) v.getParent();
            CheckBox chkBox = (CheckBox) v;
            ((Bundle) chkBox.getTag()).putBoolean((String) parent.getTag(), chkBox.isChecked());
        }
    }

    /**
     * ViewClickListener of the Buttons in mainsearch
     */
    public static class MainSearchAddButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), OnlineSearch.class);
            ((Activity) v.getTag()).startActivity(intent);
        }
    }

    public static class MainSearchCategoryListButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            MainSearch activity = (MainSearch) v.getTag();
            Intent openCatList = new Intent(activity, CategoryList.class);
            activity.startActivity(openCatList);
        }
    }

    /**
     * ViewClickListener for the buttons in item detail activity
     */
    public static class ItemDetailSaveButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final View v1 = v;
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Change item content");
            builder.setMessage("are you sure to SAVE MODIFIED CONTENT of this item?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewGroup parent;
                    ContentValues updatedItem;
                    Query query;
                    Cursor item;
                    String itemId;
                    NotificationManager notiMng;
                    Activity activity = (Activity) v1.getTag();
                    parent = (ViewGroup) v1.getParent().getParent().getParent();
                    itemId = ((TextView) parent.findViewById(R.id.item_detail_id)).getText().toString();
                    query = new Query(v1.getContext());
                    item = (Cursor) ((Spinner) parent.findViewById(R.id.item_detail_category)).getSelectedItem();
                    updatedItem = new ContentValues();
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[0], itemId);
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[1], ((EditText) parent.findViewById(R.id.item_detail_name)).getText().toString());
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[2], ((EditText) parent.findViewById(R.id.item_detail_content)).getText().toString());
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[3], ((EditText) parent.findViewById(R.id.item_detail_level)).getText().toString());
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[4], (new Date()).getTime());
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[5], (new Date()).getTime() + Strategy.doubleSpacedTimeLearning(updatedItem.getAsInteger(dtbConst.ITEM_TABLE_COLS[3])));
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[6], item.getString(item.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
                    updatedItem.put(dtbConst.ITEM_TABLE_COLS[8], ((EditText) parent.findViewById(R.id.item_detail_hint)).getText().toString());
                    query.updateItemTableRow(updatedItem, itemId);
                    //Update the reminder
                    notiMng = (NotificationManager) activity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notiMng.cancel(Integer.parseInt(itemId));
                    //Terminate the adding new item activity
                    activity.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    public static class ItemDetailDeleteButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //v.getTag returns the itemDetail activity instance
            final View v1 = v;
            final Activity activity = (Activity) v1.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Delete item");
            builder.setMessage("are you sure to DELETE this item?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewGroup parent;
                    Query query;
                    String itemId;
                    parent = (ViewGroup) v1.getParent().getParent().getParent();
                    itemId = ((TextView) parent.findViewById(R.id.item_detail_id)).getText().toString();
                    query = new Query(v1.getContext());
                    //Delete the item
                    query.deleteItem(activity.getApplicationContext(), itemId);
                    Toast.makeText(v1.getContext(), "Item " + ((TextView) parent.findViewById(R.id.item_detail_name)).getText().toString() + " is deleted", Toast.LENGTH_SHORT).show();
                    //Terminate the adding new item activity
                    activity.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }

    /**
     * Onclick of up and down level button in itemdetail layout
     */
    public static class ItemDetailBackButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((Activity) v.getTag()).finish();
        }
    }

    /**
     * Onclick of up and down level button in itemdetail layout
     */
    public static class ItemDetailLevelButtonsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            RelativeLayout parent = (RelativeLayout) v.getParent();
            EditText lv = (EditText) parent.findViewById(R.id.item_detail_level);
            ImageButton bt = (ImageButton) v;
            int level = Integer.parseInt(lv.getText().toString());
            if (v.getId() == R.id.item_detail_next_level)
                level++;
            else if (level != 0)
                level--;
            lv.setText(Integer.toString(level));
        }
    }

    /**
     * ViewClickListener for addItem activity
     */
    public static class AddItemSaveButton implements View.OnClickListener {
        private EditText itemName, itemContent, itemNewCatt,itemHint;
        private Spinner itemCatt;
        private CheckBox NewCatchkb,Randomizechkb;
        private ViewGroup parent;
        private ContentValues newRow;
        private Query query;
        private long itemId;
        private ComponentName cpnName;
        private Activity activity;
        private PersistableBundle persitBundle;
        private JobInfo jobinfo;
        private JobInfo.Builder jobInfoBuilder;
        private JobScheduler scheduler;

        @Override
        public void onClick(View v) {
            //Initiating things
            parent = (ViewGroup) v.getParent().getParent().getParent();
            newRow = new ContentValues();
            query = new Query(v.getContext());
            itemName = (EditText) parent.findViewById(R.id.add_item_name);
            itemContent = (EditText) parent.findViewById(R.id.add_item_content);
            itemNewCatt = (EditText) parent.findViewById(R.id.add_item_new_category);
            itemHint=(EditText)parent.findViewById(R.id.add_item_hint);
            itemCatt = (Spinner) parent.findViewById(R.id.add_item_category);
            NewCatchkb = (CheckBox) parent.findViewById(R.id.add_item_new_category_check_box);
            Randomizechkb=(CheckBox)parent.findViewById(R.id.add_item_randomize_check_box);
            activity = (Activity) v.getTag();
            persitBundle = new PersistableBundle();
            //Collect data of the new item
            if((newRow = collectData(Randomizechkb.isChecked()))==null)
                return;
            //Save the item
            itemId = query.insertItem(newRow);
            newRow.put(dtbConst.ITEM_TABLE_COLS[0], itemId);
            Toast.makeText(parent.getContext(), "New item is created", Toast.LENGTH_SHORT).show();
            //Setting up the reminder for this item
//            ReminderService.scheduleReminder(parent.getContext(), newRow);
            //Close the Activity and go back to main search
            if(v.getId()==parent.findViewById(R.id.add_item_save).getId())
                activity.finish();
            else{
                itemName.setText("");
                itemContent.setText("");
                itemHint.setText("");
            }
        }

        private ContentValues collectData(boolean isRandomized) {
            ContentValues newItem = new ContentValues();
            newItem.put(dtbConst.ITEM_TABLE_COLS[1], itemName.getText().toString());
            newItem.put(dtbConst.ITEM_TABLE_COLS[2], itemContent.getText().toString());
            newItem.put(dtbConst.ITEM_TABLE_COLS[3], 0);
            newItem.put(dtbConst.ITEM_TABLE_COLS[4], (new Date()).getTime());
            if(isRandomized)
                newItem.put(dtbConst.ITEM_TABLE_COLS[5], (MaintainanceService.TodayTimeMark() + Strategy.randomFirstRemind()));
            else
                newItem.put(dtbConst.ITEM_TABLE_COLS[5], (MaintainanceService.TodayTimeMark() + Strategy.doubleSpacedTimeLearning(0)));
            if (NewCatchkb.isChecked()) {
                if(itemNewCatt.getText().toString().isEmpty()){
                    Toast.makeText(activity, "Please choose a name for the new category", Toast.LENGTH_SHORT).show();
                    return null;
                }else
                    newItem.put(dtbConst.ITEM_TABLE_COLS[6], itemNewCatt.getText().toString());
            }else {
                Cursor item = (Cursor) itemCatt.getSelectedItem();
                newItem.put(dtbConst.ITEM_TABLE_COLS[6], item.getString(item.getColumnIndex(dtbConst.CATEGORY_TABLE_COLS[1])));
            }
            newItem.put(dtbConst.ITEM_TABLE_COLS[7], (new Date()).getTime());
            newItem.put(dtbConst.ITEM_TABLE_COLS[8], itemHint.getText().toString());
            return newItem;
        }
    }

    public static class AddItemCancelButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ((Activity) v.getTag()).finish();
        }
    }

    public static class AddItemCheckBoxNewCategory implements View.OnClickListener {
        public final float DISABLE_ITEM_ALPHA = 0.2f;

        @Override
        public void onClick(View v) {
            LinearLayout parent = (LinearLayout) v.getParent();
            Spinner catt = (Spinner) parent.findViewById(R.id.add_item_category);
            EditText newCatt = (EditText) parent.findViewById(R.id.add_item_new_category);
            CheckBox chkb = (CheckBox) v;
            if (chkb.isChecked()) {
                newCatt.setAlpha(1);
                newCatt.setFocusable(true);
                newCatt.setFocusableInTouchMode(true);
                catt.setFocusableInTouchMode(false);
                catt.setFocusable(false);
                catt.setAlpha(DISABLE_ITEM_ALPHA);
            } else {
                newCatt.setAlpha(DISABLE_ITEM_ALPHA);
                newCatt.setFocusable(false);
                newCatt.setFocusableInTouchMode(false);
                catt.setFocusableInTouchMode(true);
                catt.setFocusable(true);
                catt.setAlpha(1);
            }
        }
    }

    public static class CategoryListItemOnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LinearLayout parent=(LinearLayout)v;
            Activity activity=(Activity)parent.getTag();
            activity.startActivity((Intent) parent.findViewById(R.id.list_view_item_name).getTag());
        }
    }
    public static class CategoryDetailSaveButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final View v1 = v;
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Change category information");
            builder.setMessage("are you sure to SAVE MODIFIED CONTENT of this category?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewGroup parent;
                    ContentValues updatedItem;
                    Query query;
                    String catId;
                    NotificationManager notiMng;
                    Activity activity = (Activity) v1.getTag();
                    parent = (ViewGroup) v1.getParent().getParent().getParent();
                    catId = ((TextView) parent.findViewById(R.id.category_detail_id)).getText().toString();
                    query = new Query(v1.getContext());
                    updatedItem = new ContentValues();
                    updatedItem.put(dtbConst.CATEGORY_TABLE_COLS[0], catId);
                    updatedItem.put(dtbConst.CATEGORY_TABLE_COLS[1], ((EditText) parent.findViewById(R.id.category_detail_name)).getText().toString());
                    query.updateCategoryItemCategoryName(query.getCategoryById(catId).getString(1), updatedItem.getAsString(dtbConst.CATEGORY_TABLE_COLS[1]));
                    query.updateCategoryTableRow(updatedItem, catId);
                    //Terminate the adding new item activity
                    activity.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }
    public static class CategoryDetailDeleteButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //v.getTag returns the itemDetail activity instance
            final View v1 = v;
            final Activity activity = (Activity) v1.getTag();
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Delete category");
            builder.setMessage("are you sure to DELETE this category");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewGroup parent;
                    Query query;
                    String itemId;
                    parent = (ViewGroup) v1.getParent().getParent().getParent();
                    itemId = ((TextView) parent.findViewById(R.id.category_detail_id)).getText().toString();
                    query = new Query(v1.getContext());
                    //Delete the item
                    query.deleteCategory(itemId);
                    Toast.makeText(v1.getContext(), "Category " + ((TextView) parent.findViewById(R.id.category_detail_name)).getText().toString() + " is deleted", Toast.LENGTH_SHORT).show();
                    //Terminate the adding new item activity
                    activity.finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.show();
        }
    }
    public static class OnlineSearchOnlineItemOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Context ct=v.getContext();
            Entry entry=(Entry)v.getTag();
            Intent addOnlineItem=new Intent(ct,AddItem.class);
            addOnlineItem.setAction(AddItem.INTENT_START_ADD_ONLINE_ITEM_KEY);
            addOnlineItem.putExtra(dtbConst.ITEM_TABLE_COLS[1], entry.getName());
            addOnlineItem.putExtra(dtbConst.ITEM_TABLE_COLS[2], entry.getContent());
            addOnlineItem.putExtra(dtbConst.ITEM_TABLE_COLS[8], entry.getHint());
            ct.startActivity(addOnlineItem);
        }
    }
    public static class OnlineSearchOfflineItemOnClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Context ct=v.getContext();
            Entry entry=(Entry)v.getTag();
            Intent openOfflineItem=new Intent(ct,ItemDetail.class);
            //the _id of the item is embeded into the field source of the new Entry instance
            openOfflineItem.putExtra(ItemDetail.INTENT_START_ITEM_DETAIL_KEY, Integer.parseInt(entry.getSource()));
            ct.startActivity(openOfflineItem);
        }
    }

    public static class OnlineAddManually implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(v.getContext(),AddItem.class);
            intent.setAction(AddItem.INTENT_START_ADD_ITEM_KEY);
            v.getContext().startActivity(intent);
        }
    }
}