package br.com.tiagohs.popmovies.presenter;

import android.content.Context;

import java.util.List;

import br.com.tiagohs.popmovies.model.dto.GenrerMoviesDTO;
import br.com.tiagohs.popmovies.view.PerfilEstatisticasView;

public interface PerfilEstatisticasPresenter extends BasePresenter<PerfilEstatisticasView> {

    void initUpdates();
    void setContext(Context context);
}
