package br.com.tiagohs.popmovies.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.twitter.sdk.android.core.models.User;

import java.util.Calendar;

import br.com.tiagohs.popmovies.data.repository.ProfileRepository;
import br.com.tiagohs.popmovies.model.db.ProfileDB;
import br.com.tiagohs.popmovies.model.db.UserDB;
import br.com.tiagohs.popmovies.util.ImageUtils;
import br.com.tiagohs.popmovies.util.PrefsUtils;
import br.com.tiagohs.popmovies.util.ViewUtils;
import br.com.tiagohs.popmovies.view.PerfilEditView;

public class PerfilEditPresenterImpl implements PerfilEditPresenter {

    private Context mContext;
    private PerfilEditView mPerfilEditView;

    private ProfileRepository mProfileRepository;
    private ProfileDB mProfileDB;

    @Override
    public void setView(PerfilEditView view) {
        mPerfilEditView = view;
    }

    public void setContext(Context context) {
        mContext = context;
        mProfileRepository = new ProfileRepository(context);
        mProfileDB = PrefsUtils.getCurrentProfile(mContext);
    }

    public void getProfileInfo() {

        if (!ViewUtils.isEmptyValue(mProfileDB.getUser().getNome())) {
            mPerfilEditView.setName(mProfileDB.getUser().getNome());
        }

        if (!ViewUtils.isEmptyValue(mProfileDB.getDescricao())) {
            mPerfilEditView.setDescricao(mProfileDB.getDescricao());
        }

        if (!ViewUtils.isEmptyValue(mProfileDB.getCountry())) {
            mPerfilEditView.setCountry(mProfileDB.getCountry());
        }

        if (mProfileDB.getBirthday() != null)
            mPerfilEditView.setBirthday(mProfileDB.getBirthday());

        if (mProfileDB.getUser().getTypePhoto() == UserDB.PHOTO_ONLINE)
            mPerfilEditView.setPhoto(mProfileDB.getUser().getPicturePath());
        else if (mProfileDB.getUser().getTypePhoto() == UserDB.PHOTO_LOCAL)
            mPerfilEditView.setLocalPhoto(ImageUtils.getBitmapFromPath(mProfileDB.getUser().getLocalPicture(), mContext));

    }

    @Override
    public void save(String name, Calendar birthday, String country, String gender, String descricao, String photo) {

        if (!ViewUtils.isEmptyValue(name))
            mProfileDB.getUser().setNome(name);

        if (birthday != null)
            mProfileDB.setBirthday(birthday);

        if (!ViewUtils.isEmptyValue(country))
            mProfileDB.setCountry(country);

        if (!ViewUtils.isEmptyValue(gender))
            mProfileDB.setGenrer(gender);

        if (!ViewUtils.isEmptyValue(descricao))
            mProfileDB.setDescricao(descricao);

        if (!ViewUtils.isEmptyValue(photo)) {
            mProfileDB.getUser().setLocalPicture(photo);
            mProfileDB.getUser().setTypePhoto(UserDB.PHOTO_LOCAL);
        }

        mProfileRepository.saveProfile(mProfileDB, mContext);
    }


    @Override
    public void onCancellRequest(Activity activity, String tag) {

    }
}
