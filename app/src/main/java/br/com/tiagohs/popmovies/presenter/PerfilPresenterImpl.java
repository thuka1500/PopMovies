package br.com.tiagohs.popmovies.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;

import java.util.Random;

import br.com.tiagohs.popmovies.App;
import br.com.tiagohs.popmovies.data.repository.MovieRepository;
import br.com.tiagohs.popmovies.model.db.MovieDB;
import br.com.tiagohs.popmovies.model.db.ProfileDB;
import br.com.tiagohs.popmovies.model.movie.MovieDetails;
import br.com.tiagohs.popmovies.model.response.ImageResponse;
import br.com.tiagohs.popmovies.server.ResponseListener;
import br.com.tiagohs.popmovies.server.methods.MoviesServer;
import br.com.tiagohs.popmovies.util.PrefsUtils;
import br.com.tiagohs.popmovies.view.MovieDetailsView;
import br.com.tiagohs.popmovies.view.PerfilView;

public class PerfilPresenterImpl implements PerfilPresenter, ResponseListener<ImageResponse> {

    private PerfilView mPerfilView;

    private MovieRepository mMovieRepository;
    private Context mContext;

    private ProfileDB mProfile;
    private MoviesServer mMoviesServer;
    private String mTag;

    public PerfilPresenterImpl() {
        mMoviesServer = new MoviesServer();
    }

    public void setContext(Context context) {
        mContext = context;

        mMovieRepository = new MovieRepository(mContext);
    }

    public void initUpdates(String tag) {
        int duaracaoTotalAssistidas = 0;
        mTag = tag;

        mProfile = PrefsUtils.getCurrentProfile(mContext);
        mProfile.setFilmesAssistidos(mMovieRepository.findAllMoviesDB(mProfile.getProfileID()));
        mProfile.setFilmesFavoritos(mMovieRepository.findAllFavoritesMovies(mProfile.getProfileID()));

        if (!mProfile.getFilmesAssistidos().isEmpty()) {
            int index = new Random().nextInt(mProfile.getFilmesAssistidos().size());
            getMovie(mProfile.getFilmesAssistidos().get(index).getIdServer(), tag);
        }

        mPerfilView.setEmailPerfil(mProfile.getUser().getEmail());
        mPerfilView.setNamePerfil(mProfile.getUser().getNome());
        mPerfilView.setImagePerfil(mProfile.getFotoPath());

        for (MovieDB movieDB : mProfile.getFilmesAssistidos()) {
            duaracaoTotalAssistidas += movieDB.getDuracao();
        }

        mPerfilView.setTotalHorasAssistidas(duaracaoTotalAssistidas);
        mPerfilView.setTotalFilmesAssistidos(mProfile.getFilmesAssistidos().size());
        mPerfilView.setupTabs();
        mPerfilView.setProgressVisibility(View.GONE);
    }

    private void getMovie(int movieID, String tag) {
        mMoviesServer.getMovieImagens(movieID, tag, this);
    }

    @Override
    public void setView(PerfilView view) {
        mPerfilView = view;
    }

    @Override
    public void onCancellRequest(Activity activity, String tag) {
        ((App) activity.getApplication()).cancelAll(tag);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(ImageResponse response) {
        if (!response.getBackdrops().isEmpty()) {
            int index = new Random().nextInt(response.getBackdrops().size());
            mPerfilView.setBackground(response.getBackdrops().get(index).getFilePath());
        } else {
            int index = new Random().nextInt(mProfile.getFilmesAssistidos().size());
            getMovie(mProfile.getFilmesAssistidos().get(index).getIdServer(), mTag);
        }

    }

}