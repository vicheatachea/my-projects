import React, {useEffect, useState} from 'react';
import Card from '../Card/Card.jsx';
import ErrorPrompt from '../ErrorPrompt/ErrorPrompt.jsx';
import styles from './CardList.module.css';

function CardList({title, items, error}) {
    const [currentIndex, setCurrentIndex] = useState(0);
    const visibleItems = 5; // Number of items to show at a time

    useEffect(() => {
        setCurrentIndex(0);
    }, [items]);

    const handlePrevious = () => {
        setCurrentIndex((prevIndex) => Math.max(prevIndex - visibleItems, 0));
    };

    const handleNext = () => {
        setCurrentIndex((prevIndex) =>
            Math.min(prevIndex + visibleItems, items.length - visibleItems)
        );
    };

    const canGoPrevious = currentIndex > 0;
    const canGoNext = currentIndex + visibleItems < (items?.length || 0);

    if (error) {
        return <ErrorPrompt error={error}/>;
    }

    return (
        <section className={styles.cards}>
            <h2 className={styles.sectionTitle}>{title}</h2>
            <div className={styles.container}>
                <button className={`${styles.arrow} ${!canGoPrevious ? styles.hiddenArrow : ''}`}
                        onClick={handlePrevious}>❮
                </button>
                <div className={styles.cardList}>
                    {items?.slice(currentIndex, currentIndex + visibleItems).map(item => (
                        <Card key={item.id} item={item} artists={item.artists}/>
                    ))}
                </div>
                <button className={`${styles.arrow} ${!canGoNext ? styles.hiddenArrow : ''}`}
                        onClick={handleNext}>❯
                </button>
            </div>
        </section>
    );
}

export default CardList;
