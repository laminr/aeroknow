//package biz.eventually.atpl.dagger.module;
//
//import com.squareup.moshi.JsonAdapter;
//import com.squareup.moshi.Moshi;
//
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Type;
//
//import retrofit2.Call;
//import retrofit2.CallAdapter;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.HttpException;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//import rx.Observable;
//import rx.Scheduler;
//import rx.functions.Func1;
//
//
///**
// * Permet de définir le subscribeScheduler et le observerScheduler par défaut pour le retrofit
// */
//public class RxThreadCallAdapter
//	extends CallAdapter.Factory
//{
//	private RxJavaCallAdapterFactory rxFactory = RxJavaCallAdapterFactory.create();
//
//	private Scheduler subscribeScheduler;
//
//	private Scheduler observerScheduler;
//
//	public RxThreadCallAdapter(final Scheduler subscribeScheduler, final Scheduler observerScheduler)
//	{
//		this.subscribeScheduler = subscribeScheduler;
//		this.observerScheduler = observerScheduler;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public CallAdapter<?> get(final Type returnType, final Annotation[] annotations, final Retrofit retrofit)
//	{
//		CallAdapter<Observable<?>> callAdapter = (CallAdapter<Observable<?>>) rxFactory.get(returnType, annotations, retrofit);
//		return callAdapter != null ? new ThreadCallAdapter(callAdapter) : null;
//	}
//
//	private final class ThreadCallAdapter
//		implements CallAdapter<Observable<?>>
//	{
//		CallAdapter<Observable<?>> delegateAdapter;
//
//		ThreadCallAdapter(final CallAdapter<Observable<?>> delegateAdapter)
//		{
//			this.delegateAdapter = delegateAdapter;
//		}
//
//		@Override
//		public Type responseType()
//		{
//			return delegateAdapter.responseType();
//		}
//
//		@SuppressWarnings("unchecked")
//		@Override
//		public <T> Observable<?> adapt(final Call<T> call)
//		{
//			return ((Observable) delegateAdapter.adapt(call))
//				.subscribeOn(subscribeScheduler)
//				.observeOn(observerScheduler)
//				.onErrorResumeNext(new Func1<Throwable, Observable>()
//				{
//					@Override
//					public Observable call(final Throwable throwable)
//					{
//						Throwable exception;
//						// We had non-200 http error
//						if ( throwable instanceof HttpException ) {
//							exception = new PvoException(this.getClass(), ExceptionType.HTTP,
//								"Erreur lors de l'appel à " + ((HttpException) throwable).response().raw().request().url().toString(), throwable);
//
//							try {
//								Moshi moshi = new Moshi.Builder().build();
//								JsonAdapter<HttpErrorBody> jsonAdapter = moshi.adapter(HttpErrorBody.class);
//								String body = ((HttpException) throwable).response().errorBody().string();
//								HttpErrorBody httpErrorBody = jsonAdapter.fromJson(body);
//								((PvoException) exception).setErrorBody(httpErrorBody);
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//						// A network error happened
//						else if ( throwable instanceof IOException ) {
//							exception = new PvoException(this.getClass(), ExceptionType.NETWORK, "Erreur réseau", throwable);
//						}
//						// We don't know what happened
//						else {
//							exception = new PvoException(this.getClass(), ExceptionType.UNKNOWN, "Erreur inconnu", throwable);
//						}
//						// Gestion de l'exception
//						ExceptionManager.manage(exception);
//						return Observable.error(exception);
//					}
//				});
//		}
//	}
//}