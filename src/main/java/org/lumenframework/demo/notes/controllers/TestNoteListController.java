package org.lumenframework.demo.notes.controllers;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lumen.system.Annotations.Controller;
import lumen.system.Annotations.Key;
import lumen.system.Annotations.LumenEnabled;

import org.lumenframework.demo.notes.DataStore;
import org.lumenframework.demo.notes.Note;

import com.google.common.collect.Lists;
import com.j256.ormlite.dao.*;

@Controller
public class TestNoteListController {

	private static String replaceAllRegex(String source, String pattern, String replacement) {
	    if (source == null)
	          return null;
	    Pattern regex = Pattern.compile(pattern);
	    Matcher matcher = regex.matcher(source);
	    return matcher.replaceAll(replacement);
	}
	
    @LumenEnabled
    public static void deleteNotesByKey(@Key("key") String key) throws Exception {
        Dao<Note, Long> noteDao = DaoManager.createDao(DataStore.getInstance().getConnectionSource(), Note.class);
        List<Long> ids = Lists.newArrayList();
        GenericRawResults<String[]> searchResults = noteDao.queryRaw("SELECT KEYS FROM FT_SEARCH_DATA(?,0,0)", key);
        try {
            for (String[] row : searchResults) {
                ids.add(Long.parseLong(replaceAllRegex(row[0], "[()]", "")));
            }
        } finally {
            searchResults.close();
        }
        noteDao.deleteIds(ids);
    }
}
