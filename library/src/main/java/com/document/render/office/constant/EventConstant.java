
package com.document.render.office.constant;

public final class EventConstant {


    public static final int SYS_ONBACK_ID = 0x00000000;

    public static final int SYS_FILEPAHT_ID = SYS_ONBACK_ID + 1;

    public static final int SYS_RECENTLY_FILES_ID = SYS_FILEPAHT_ID + 1;

    public static final int SYS_MARK_FILES_ID = SYS_RECENTLY_FILES_ID + 1;

    public static final int SYS_MEMORY_CARD_ID = SYS_MARK_FILES_ID + 1;

    public static final int SYS_SEARCH_ID = SYS_MEMORY_CARD_ID + 1;

    public static final int SYS_SETTINGS_ID = SYS_SEARCH_ID + 1;

    public static final int SYS_ACCOUNT_ID = SYS_SETTINGS_ID + SYS_SETTINGS_ID;

    public static final int SYS_REGISTER_ID = SYS_ACCOUNT_ID + 1;

    public static final int SYS_UPDATE_ID = SYS_REGISTER_ID + 1;

    public static final int SYS_HELP_ID = SYS_UPDATE_ID + 1;

    public static final int SYS_ABOUT_ID = SYS_HELP_ID + 1;

    public static final int SYS_SHOW_TOOLTIP = SYS_ABOUT_ID + 1;

    public static final int SYS_CLOSE_TOOLTIP = SYS_SHOW_TOOLTIP + 1;

    public static final int SYS_INIT_ID = SYS_CLOSE_TOOLTIP + 1;

    public static final int SYS_UPDATE_TOOLSBAR_BUTTON_STATUS = SYS_INIT_ID + 1;

    public static final int SYS_SET_MAX_RECENT_NUMBER = SYS_UPDATE_TOOLSBAR_BUTTON_STATUS + 1;

    public static final int SYS_AUTO_TEST_FINISH_ID = SYS_SET_MAX_RECENT_NUMBER + 1;

    public static final int SYS_READER_FINSH_ID = SYS_AUTO_TEST_FINISH_ID + 1;

    public static final int SYS_START_BACK_READER_ID = SYS_READER_FINSH_ID + 1;

    public static final int SYS_RESET_TITLE_ID = SYS_START_BACK_READER_ID + 1;

    public static final int SYS_SET_PROGRESS_BAR_ID = SYS_RESET_TITLE_ID + 1;

    public static final int SYS_VECTORGRAPH_PROGRESS = SYS_SET_PROGRESS_BAR_ID + 1;




    public static final int FILE_CREATE_FOLDER_ID = 0x10000000;

    public static final int FILE_RENAME_ID = FILE_CREATE_FOLDER_ID + 1;

    public static final int FILE_COPY_ID = FILE_RENAME_ID + 1;

    public static final int FILE_CUT_ID = FILE_COPY_ID + 1;

    public static final int FILE_PASTE_ID = FILE_CUT_ID + 1;

    public static final int FILE_DELETE_ID = FILE_PASTE_ID + 1;

    public static final int FILE_SHARE_ID = FILE_DELETE_ID + 1;

    public static final int FILE_SORT_ID = FILE_SHARE_ID + 1;

    public static final int FILE_MARK_STAR_ID = FILE_SORT_ID + 1;

    public static final int FILE_PRINT_ID = FILE_MARK_STAR_ID + 1;

    public static final int FILE_REFRESH_ID = FILE_PRINT_ID + 1;

    public static final int FILE_SORT_TYPE_ID = FILE_REFRESH_ID + 1;

    public static final int FILE_CREATE_FOLDER_FAILED_ID = FILE_SORT_TYPE_ID + 1;




    public static final int APP_FIND_ID = 0x20000000;

    public static final int APP_SHARE_ID = APP_FIND_ID + 1;

    public static final int APP_INTERNET_SEARCH_ID = APP_SHARE_ID + 1;

    public static final int APP_READ_ID = APP_INTERNET_SEARCH_ID + 1;

    public static final int APP_APPROVE_ID = APP_READ_ID + 1;

    public static final int APP_ZOOM_ID = APP_APPROVE_ID + 1;

    public static final int APP_FIT_ZOOM_ID = APP_ZOOM_ID + 1;

    public static final int APP_CONTENT_SELECTED = APP_FIT_ZOOM_ID + 1;

    public static final int APP_HYPERLINK = APP_CONTENT_SELECTED + 1;

    public static final int APP_ABORTREADING = APP_HYPERLINK + 1;

    public static final int APP_GENERATED_PICTURE_ID = APP_ABORTREADING + 1;

    public static final int APP_COUNT_PAGES_ID = APP_GENERATED_PICTURE_ID + 1;

    public static final int APP_CURRENT_PAGE_NUMBER_ID = APP_COUNT_PAGES_ID + 1;

    public static final int APP_PAGE_UP_ID = APP_CURRENT_PAGE_NUMBER_ID + 1;

    public static final int APP_PAGE_DOWN_ID = APP_PAGE_UP_ID + 1;

    public static final int APP_COUNT_PAGES_CHANGE_ID = APP_PAGE_DOWN_ID + 1;

    public static final int APP_THUMBNAIL_ID = APP_COUNT_PAGES_CHANGE_ID + 1;

    public static final int APP_AUTHENTICATE_PASSWORD = APP_THUMBNAIL_ID + 1;

    public static final int APP_PASSWORD_OK_INIT = APP_AUTHENTICATE_PASSWORD + 1;

    public static final int APP_PAGEAREA_TO_IMAGE = APP_PASSWORD_OK_INIT + 1;

    public static final int APP_GET_HYPERLINK_URL_ID = APP_PAGEAREA_TO_IMAGE + 1;

    public static final int APP_SET_FIT_SIZE_ID = APP_GET_HYPERLINK_URL_ID + 1;

    public static final int APP_GET_FIT_SIZE_STATE_ID = APP_SET_FIT_SIZE_ID + 1;

    public static final int APP_GET_REAL_PAGE_COUNT_ID = APP_GET_FIT_SIZE_STATE_ID + 1;

    public static final int APP_GET_SNAPSHOT_ID = APP_GET_REAL_PAGE_COUNT_ID + 1;

    public static final int APP_DRAW_ID = APP_GET_SNAPSHOT_ID + 1;

    public static final int APP_BACK_ID = APP_DRAW_ID + 1;

    public static final int APP_PEN_ID = APP_BACK_ID + 1;

    public static final int APP_ERASER_ID = APP_PEN_ID + 1;

    public static final int APP_COLOR_ID = APP_ERASER_ID + 1;

    public static final int APP_INIT_CALLOUTVIEW_ID = APP_COLOR_ID + 1;




    public static final int APP_FINDING = 0x2F000000;

    public static final int APP_FIND_BACKWARD = APP_FINDING + 1;

    public static final int APP_FIND_FORWARD = APP_FIND_BACKWARD + 1;



    public static final int WP_SELECT_TEXT_ID = 0x30000000;

    public static final int WP_SWITCH_VIEW = WP_SELECT_TEXT_ID + 1;

    public static final int WP_SHOW_PAGE = WP_SWITCH_VIEW + 1;

    public static final int WP_PAGE_TO_IMAGE = WP_SHOW_PAGE + 1;

    public static final int WP_GET_PAGE_SIZE = WP_PAGE_TO_IMAGE + 1;

    public static final int WP_LAYOUT_NORMAL_VIEW = WP_GET_PAGE_SIZE + 1;

    public static final int WP_GET_VIEW_MODE = WP_LAYOUT_NORMAL_VIEW + 1;

    public static final int WP_PRINT_MODE = WP_GET_VIEW_MODE + 1;

    public static final int WP_LAYOUT_COMPLETED = WP_PRINT_MODE + 1;




    public static final int SS_SHEET_CHANGE = 0x40000000;

    public static final int SS_SHOW_SHEET = SS_SHEET_CHANGE + 1;

    public static final int SS_GET_ALL_SHEET_NAME = SS_SHOW_SHEET + 1;

    public static final int SS_GET_SHEET_NAME = SS_GET_ALL_SHEET_NAME + 1;

    public static final int SS_CHANGE_SHEET = SS_GET_SHEET_NAME + 1;

    public static final int SS_REMOVE_SHEET_BAR = SS_CHANGE_SHEET + 1;



    public static final int PG_NOTE_ID = 0x50000000;

    public static final int PG_REPAINT_ID = PG_NOTE_ID + 1;

    public static final int PG_SHOW_SLIDE_ID = PG_REPAINT_ID + 1;

    public static final int PG_SLIDE_TO_IMAGE = PG_SHOW_SLIDE_ID + 1;

    public static final int PG_GET_SLIDE_NOTE = PG_SLIDE_TO_IMAGE + 1;

    public static final int PG_GET_SLIDE_SIZE = PG_GET_SLIDE_NOTE + 1;

    public static final int PG_SLIDESHOW = 0x51000000;
    public static final int PG_SLIDESHOW_GEGIN = PG_SLIDESHOW + 1;
    public static final int PG_SLIDESHOW_END = PG_SLIDESHOW_GEGIN + 1;
    public static final int PG_SLIDESHOW_PREVIOUS = PG_SLIDESHOW_END + 1;
    public static final int PG_SLIDESHOW_NEXT = PG_SLIDESHOW_PREVIOUS + 1;
    public static final int PG_SLIDESHOW_HASPREVIOUSACTION = PG_SLIDESHOW_NEXT + 1;
    public static final int PG_SLIDESHOW_HASNEXTACTION = PG_SLIDESHOW_HASPREVIOUSACTION + 1;
    public static final int PG_SLIDESHOW_DURATION = PG_SLIDESHOW_HASNEXTACTION + 1;
    public static final int PG_SLIDESHOW_SLIDEEXIST = PG_SLIDESHOW_DURATION + 1;
    public static final int PG_SLIDESHOW_ANIMATIONSTEPS = PG_SLIDESHOW_SLIDEEXIST + 1;
    public static final int PG_SLIDESHOW_SLIDESHOWTOIMAGE = PG_SLIDESHOW_ANIMATIONSTEPS + 1;



    public static final int PDF_SHOW_PAGE = 0x60000000;

    public static final int PDF_PAGE_TO_IMAGE = PDF_SHOW_PAGE + 1;

    public static final int PDF_GET_PAGE_SIZE = PDF_PAGE_TO_IMAGE + 1;



    public static final int TXT_DIALOG_FINISH_ID = 0x7000000;

    public static final int TXT_REOPNE_ID = TXT_DIALOG_FINISH_ID + 1;



    public static final int TEST_REPAINT_ID = 0xF0000000;

}
