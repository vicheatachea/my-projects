import React from 'react';
import styles from './ErrorPrompt.module.css';

function ErrorPrompt({error}) {
    const handleConnect = () => {
        window.location.href = '/api/spotify/login';
    };

    return (
        <section className={styles.prompt}>
            <p className={styles.text}>
                {error === 401 ? 'Connect to your Spotify account to see results' : 'An error occurred. Please try again.'}
            </p>
            {error === 401 && (
                <button className={styles.button} onClick={handleConnect}>
                    Connect to Spotify
                </button>
            )}
        </section>
    );
}

export default ErrorPrompt;
