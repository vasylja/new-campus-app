package de.tum.in.newtumcampus.tumonline;

/**
 * this interface should frame, how to implement a listener for the fetchInteractive method in TUMOnlineRequest
 * 
 * @author Daniel G. Mayr
 * 
 */
public interface TUMOnlineRequestFetchListener {

	/**
	 * fetchInteractive will call this method if the fetch of the TUMOnlineRequest has succeeded
	 * 
	 * @param rawResponse
	 *            this will be the raw return of the fetch
	 */
	public void onFetch(String rawResponse);

	/**
	 * if the fetchInteractive method will result in null or there is no internet connection then this method will be called
	 * 
	 * @param errorReason
	 *            the reason why the request failed (localized)
	 */
	public void onFetchError(String errorReason);

	public void onFetchCancelled();

}
