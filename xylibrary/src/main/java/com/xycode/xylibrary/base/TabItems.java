package com.xycode.xylibrary.base;

/**
 * Created by XY on 2016-08-09.
 */
public abstract class TabItems {

  /*  protected Context context;
    protected TabLayout tabMain;
    protected int tabIconsNormalArrayId;
    protected int tabIconsSelectedArrayId;
    protected String[] titles;
    protected TypedArray tabIconsNormal;
    protected TypedArray tabIconsSelected;
    protected int selectedTextColor = 0;
    protected int normalTextColor = 0;

    public TabItems(Context context, TabLayout tabMain, String[] titles, int tabIconsNormalArrayId, int tabIconsSelectedArrayId, int selectedTextColor, int normalTextColor) {
        this.context = context;
        this.tabMain = tabMain;
        this.tabIconsNormalArrayId = tabIconsNormalArrayId;
        this.tabIconsSelectedArrayId = tabIconsSelectedArrayId;
        this.titles = titles;
        this.selectedTextColor = selectedTextColor;
        this.normalTextColor = normalTextColor;
        this.tabIconsNormal = context.getResources().obtainTypedArray(this.tabIconsNormalArrayId);
        this.tabIconsSelected = context.getResources().obtainTypedArray(this.tabIconsSelectedArrayId);
        setupTabIcons();
    }

    private void setupTabIcons() {
        int currentIndex = tabMain.getSelectedTabPosition();
        if (currentIndex == -1) currentIndex = 0;
        for (int i = 0; i < titles.length; i++) {
            View view = LayoutInflater.from(context).inflate(layoutId, null);
            TextView tvTab = (TextView) view.findViewById(textViewId);
            ImageView ivTab = (ImageView) view.findViewById(imageViewId);
            if (ivTab != null) {
                ivTab.setImageResource(i == currentIndex ? tabIconsSelected.getResourceId(i, 0) : tabIconsNormal.getResourceId(i, 0));
            }
            if (tvTab != null) {
                tvTab.setText(titles[i]);
                tvTab.setTextColor(context.getResources().getColor(i == currentIndex ? selectedTextColor : normalTextColor));
            }
            TabLayout.Tab tab;
            if (tabMain.getTabCount() == i) {
                tab = tabMain.newTab();
                tabMain.addTab(tab);
            } else {
                tab = tabMain.getTabAt(i);
            }
            initTabItemView(layoutId, view, i);
            tab.setCustomView(view);
        }
    }

    public int setTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        View view = tab.getCustomView();
        TextView tvTab = (TextView) view.findViewById(textViewId);
        ImageView ivTab = (ImageView) view.findViewById(imageViewId);
        ivTab.setImageResource(tabIconsSelected.getResourceId(position, 0));
        tvTab.setTextColor(context.getResources().getColor(selectedTextColor));
        return position;
    }

    public void setTabUnselected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        View view = tab.getCustomView();
        TextView tvTab = (TextView) view.findViewById(textViewId);
        ImageView ivTab = (ImageView) view.findViewById(imageViewId);
        ivTab.setImageResource(tabIconsNormal.getResourceId(position, 0));
        tvTab.setTextColor(context.getResources().getColor(normalTextColor));
    }

    public View getTabViewItemById(int pos, int viewId) {
        if (tabMain.getTabCount() > pos) {
            return tabMain.getTabAt(pos).getCustomView().findViewById(viewId);
        }
        return null;
    }*/
}
