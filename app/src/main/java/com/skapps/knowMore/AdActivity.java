package com.skapps.knowMore;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.internal.util.client.zzb;
import com.google.android.gms.internal.zzfu;
import com.google.android.gms.internal.zzfv;

public class AdActivity extends Activity {
    public static final String CLASS_NAME = "com.google.android.gms.ads.AdActivity";
    public static final String SIMPLE_CLASS_NAME = "AdActivity";
    private zzfv zzoA;

    public AdActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.zzoA = zzfu.createAdOverlay(this);
        if(this.zzoA == null) {
            zzb.zzaK("Could not create ad overlay.");
            this.finish();
        } else {
            try {
                this.zzoA.onCreate(savedInstanceState);
            } catch (RemoteException var3) {
                zzb.zzd("Could not forward onCreate to ad overlay:", var3);
                this.finish();
            }

        }
    }

    protected void onRestart() {
        super.onRestart();

        try {
            if(this.zzoA != null) {
                this.zzoA.onRestart();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onRestart to ad overlay:", var2);
            this.finish();
        }

    }

    protected void onStart() {
        super.onStart();

        try {
            if(this.zzoA != null) {
                this.zzoA.onStart();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onStart to ad overlay:", var2);
            this.finish();
        }

    }

    protected void onResume() {
        super.onResume();

        try {
            if(this.zzoA != null) {
                this.zzoA.onResume();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onResume to ad overlay:", var2);
            this.finish();
        }

    }

    protected void onPause() {
        try {
            if(this.zzoA != null) {
                this.zzoA.onPause();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onPause to ad overlay:", var2);
            this.finish();
        }

        super.onPause();
    }

    protected void onSaveInstanceState(Bundle outState) {
        try {
            if(this.zzoA != null) {
                this.zzoA.onSaveInstanceState(outState);
            }
        } catch (RemoteException var3) {
            zzb.zzd("Could not forward onSaveInstanceState to ad overlay:", var3);
            this.finish();
        }

        super.onSaveInstanceState(outState);
    }

    protected void onStop() {
        try {
            if(this.zzoA != null) {
                this.zzoA.onStop();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onStop to ad overlay:", var2);
            this.finish();
        }

        super.onStop();
    }

    protected void onDestroy() {
        try {
            if(this.zzoA != null) {
                this.zzoA.onDestroy();
            }
        } catch (RemoteException var2) {
            zzb.zzd("Could not forward onDestroy to ad overlay:", var2);
        }

        super.onDestroy();
    }

    private void zzaD() {
        if(this.zzoA != null) {
            try {
                this.zzoA.zzaD();
            } catch (RemoteException var2) {
                zzb.zzd("Could not forward setContentViewSet to ad overlay:", var2);
            }
        }

    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.zzaD();
    }

    public void setContentView(View view) {
        super.setContentView(view);
        this.zzaD();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.zzaD();
    }

    public void onBackPressed() {
        boolean var1 = true;

        try {
            if(this.zzoA != null) {
                var1 = this.zzoA.zzfn();
            }
        } catch (RemoteException var3) {
            zzb.zzd("Could not forward onBackPressed to ad overlay:", var3);
        }

        if(var1) {
            super.onBackPressed();
        }

    }
}
