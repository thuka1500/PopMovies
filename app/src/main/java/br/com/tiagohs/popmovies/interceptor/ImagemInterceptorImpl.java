package br.com.tiagohs.popmovies.interceptor;

import com.android.volley.VolleyError;

import br.com.tiagohs.popmovies.model.response.ImageResponse;
import br.com.tiagohs.popmovies.server.ResponseListener;
import br.com.tiagohs.popmovies.server.methods.MoviesServer;

public class ImagemInterceptorImpl implements ResponseListener<ImageResponse>, ImagemInterceptor {
    private MoviesServer mMoviesServer;
    private onImagemListener mListener;

    public ImagemInterceptorImpl(onImagemListener listener) {
        this.mMoviesServer = new MoviesServer();
        this.mListener = listener;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mListener.onImageRequestError(error);
    }

    @Override
    public void onResponse(ImageResponse response) {
        mListener.onImageRequestSucess(response);
    }

    @Override
    public void getImagens(int movieID, String tag) {
        mMoviesServer.getMovieImagens(movieID, tag, this);
    }
}
