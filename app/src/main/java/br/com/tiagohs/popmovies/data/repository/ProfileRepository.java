package br.com.tiagohs.popmovies.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.tiagohs.popmovies.R;
import br.com.tiagohs.popmovies.data.PopMoviesContract;
import br.com.tiagohs.popmovies.data.PopMoviesDB;
import br.com.tiagohs.popmovies.data.SQLHelper;
import br.com.tiagohs.popmovies.model.db.MovieDB;
import br.com.tiagohs.popmovies.model.db.ProfileDB;
import br.com.tiagohs.popmovies.model.dto.GenrerMoviesDTO;
import br.com.tiagohs.popmovies.model.movie.Movie;
import br.com.tiagohs.popmovies.util.PrefsUtils;

import static android.R.attr.id;

public class ProfileRepository {
    private static final String TAG = ProfileRepository.class.getSimpleName();

    private PopMoviesDB mPopMoviesDB;
    private MovieRepository mMovieRepository;
    private UserRepository mUserRepository;

    public ProfileRepository(Context context) {
        this.mPopMoviesDB = new PopMoviesDB(context);
        this.mMovieRepository = new MovieRepository(context);
        this.mUserRepository = new UserRepository(context);
    }

    public long saveProfile(ProfileDB profile, Context context) {
        SQLiteDatabase db = null;
        long id = 0;

        try {
            ContentValues values = getProfileContentValues(profile);

            boolean userJaExistente = findProfileByUserUsername(profile.getUser().getUsername()) != null;
            db = mPopMoviesDB.getWritableDatabase();

            if (userJaExistente)
                id = db.update(PopMoviesContract.ProfileEntry.TABLE_NAME, values, SQLHelper.ProfileSQL.WHERE_PROFILE_BY_USERNAME, new String[]{profile.getUser().getUsername()});
            else
                id = db.insert(PopMoviesContract.ProfileEntry.TABLE_NAME, "", values);

            mUserRepository.saveUser(profile.getUser(), context);
            profile.setProfileID(id);
            PrefsUtils.setCurrentProfile(profile, context);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return id;
    }

    private void deleteProfile(String value, String where) {
        SQLiteDatabase db = mPopMoviesDB.getWritableDatabase();
        Log.i(TAG, "Delete Profile Chamado.");

        try {
            db.delete(PopMoviesContract.ProfileEntry.TABLE_NAME, where, new String[]{value});
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteProfileByID(long id) {
        deleteProfile(String.valueOf(id), SQLHelper.ProfileSQL.WHERE_PROFILE_BY_ID);
    }

    public void deleteProfileByUsername(String username) {
        deleteProfile(username, SQLHelper.ProfileSQL.WHERE_PROFILE_BY_USERNAME);
    }

    public ProfileDB findProfileByUserUsername(String username) {
        SQLiteDatabase db = mPopMoviesDB.getWritableDatabase();
        Log.i(TAG, "Find Profile Chamado.");

        try {
            Cursor c = db.query(PopMoviesContract.ProfileEntry.TABLE_NAME, null, SQLHelper.ProfileSQL.WHERE_PROFILE_BY_USERNAME, new String[]{username}, null, null, null);
            if (c.moveToFirst()) {

                return getProfileByCursor(c, username);
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return null;
    }

    public long getTotal(String sql, String[] values) {
        SQLiteDatabase db = mPopMoviesDB.getWritableDatabase();
        Log.i(TAG, "Total Hour Chamado.");
        long total = 0;

        try {
            Cursor c = db.rawQuery(sql, values);

            if(c.moveToFirst())
                total = c.getInt(0);
            else
                total = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return total;
    }

    private boolean hasSomeMovie(String sql, String[] values) {
        SQLiteDatabase db = mPopMoviesDB.getWritableDatabase();
        long total = 0;

        try {
            Cursor c = db.rawQuery(sql, values);

            if(c.moveToFirst())
                total = c.getInt(0);
            else
                total = 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return total == 1;
    }

    public boolean hasMoviesWatched(long profileID) {
        return hasSomeMovie(SQLHelper.ProfileSQL.SQL_HAS_MOVIE_WATCHED, new String[]{String.valueOf(profileID)});
    }

    public boolean hasMoviesWantSee(long profileID) {
        return hasSomeMovie(SQLHelper.ProfileSQL.SQL_HAS_MOVIE_WANT_SEE, new String[]{String.valueOf(profileID)});
    }

    public boolean hasMoviesDontWantSee(long profileID) {
        return hasSomeMovie(SQLHelper.ProfileSQL.SQL_HAS_MOVIE_DONT_WANT_SEE, new String[]{String.valueOf(profileID)});
    }

    public boolean hasMoviesFavorite(long profileID) {
        return hasSomeMovie(SQLHelper.ProfileSQL.SQL_HAS_MOVIE_FAVORITE, new String[]{String.valueOf(profileID)});
    }

    public long getTotalHoursWatched(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_HOURS_WATCHED, new String[]{String.valueOf(profileID)});
    }

    public long getTotalMovies(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES, new String[]{String.valueOf(profileID)});
    }

    public long getTotalMoviesWatched(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES_WATCHED, new String[]{String.valueOf(profileID)});
    }

    public long getTotalMoviesWantSee(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES_WANT_SEE, new String[]{String.valueOf(profileID)});
    }

    public long getTotalMoviesDontWantSee(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES_DONT_WANT_SEE, new String[]{String.valueOf(profileID)});
    }

    public long getTotalFavorites(long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES_FAVORITES, new String[]{String.valueOf(profileID)});
    }

    public long getTotalMoviesByGenrer(int genreID, long profileID) {
        return getTotal(SQLHelper.ProfileSQL.SQL_TOTAL_MOVIES_BY_GENRER, new String[]{String.valueOf(genreID), String.valueOf(profileID)});
    }

    public List<GenrerMoviesDTO> getAllGenrersSaved(long profileID) {
        List<GenrerMoviesDTO> genres = new ArrayList<>();
        Integer[] genrersID = new Integer[]{28, 12, 16, 35, 80, 99, 18, 10751, 14, 10769, 36, 27, 10402, 9648, 10749, 878, 10770, 53, 10752, 7};
        Integer[] namesID = new Integer[]{R.string.genere_action, R.string.genere_adventure, R.string.genere_animation, R.string.genere_comedy,
                                          R.string.genere_crime, R.string.genere_documentary, R.string.genere_drama, R.string.genere_family,
                                          R.string.genere_fantasy, R.string.genere_foreign, R.string.genere_history, R.string.genere_horror,
                                          R.string.genere_music, R.string.genere_mystery, R.string.genere_romance, R.string.genere_science_fiction,
                                          R.string.genere_tv_movie, R.string.genere_thriller, R.string.genere_war, R.string.genere_western};

        for (int cont = 0; cont < genrersID.length; cont++) {
            long totalMovies = getTotalMoviesByGenrer(genrersID[cont], profileID);

            if (totalMovies > 0) {
                GenrerMoviesDTO genrerDTO = new GenrerMoviesDTO(genrersID[cont], namesID[cont], totalMovies);

                if (!genres.contains(genrerDTO))
                    genres.add(genrerDTO);
            }

        }

        return genres;
    }

    public ProfileDB getProfileByCursor(Cursor c, String username) {
        ProfileDB profile = new ProfileDB();

        profile.setProfileID(c.getLong(c.getColumnIndex(PopMoviesContract.ProfileEntry._ID)));
        profile.setDescricao(c.getString(c.getColumnIndex(PopMoviesContract.ProfileEntry.COLUMN_DESCRIPTION)));
        profile.setProfileID(c.getInt(c.getColumnIndex(PopMoviesContract.ProfileEntry._ID)));

        profile.setFilmesAssistidos(mMovieRepository.findAllMoviesWatched(profile.getProfileID()));
        profile.setFilmesQueroVer(mMovieRepository.findAllMoviesWantSee(profile.getProfileID()));
        profile.setFilmesNaoQueroVer(mMovieRepository.findAllMoviesDontWantSee(profile.getProfileID()));
        profile.setFilmesFavoritos(mMovieRepository.findAllFavoritesMovies(profile.getProfileID()));

        profile.setTotalHorasAssistidas(getTotalHoursWatched(profile.getProfileID()));
        profile.setUser(mUserRepository.findUserByUsername(username));

        return profile;
    }

    private ContentValues getProfileContentValues(ProfileDB profile) {
        ContentValues values = new ContentValues();

        values.put(PopMoviesContract.ProfileEntry.COLUMN_DESCRIPTION, profile.getDescricao());
        values.put(PopMoviesContract.ProfileEntry.COLUMN_USER_FORER_USERNAME, profile.getUser().getUsername());

        return values;
    }
}
