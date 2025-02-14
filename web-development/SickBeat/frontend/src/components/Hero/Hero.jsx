import React from 'react';
import styles from './Hero.module.css';
import heroBackgroundLight from '../../images/hero-background-light.jpg';
import heroBackgroundDark from '../../images/hero-background-dark.jpg';

function Hero({ genresListRef }) {
    const isDarkMode = document.body.classList.contains('dark');
    const backgroundImage = isDarkMode ? heroBackgroundDark : heroBackgroundLight;

    const handleBrowseNow = () => {
        if (genresListRef.current) {
            genresListRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    };

    return (
        <section className={styles.hero}>
            <div
                className={styles.background}
                style={{ backgroundImage: `url(${backgroundImage})` }}
            >
                <div className={styles.blur}></div>
                <div className={styles.content}>
                    <h1 className={styles.phrase}>FIND MUSIC THAT MATCHES YOUR TASTE</h1>
                    <div className={styles.text}>
                        Browse through our diverse range of carefully selected songs, designed to bring out your
                        individuality and cater to your sense of style.
                    </div>
                    <div className={styles.buttonContainer}>
                        <button onClick={handleBrowseNow}>Browse Now</button>
                    </div>
                </div>
            </div>
        </section>
    );
}

export default Hero;