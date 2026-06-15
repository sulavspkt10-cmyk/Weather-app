import { useState } from 'react'
import Navbar from '../components/Navbar'
import { getWeather } from '../api'
import styles from './Home.module.css'

export default function Home() {
  const [city, setCity] = useState('')
  const [weather, setWeather] = useState(null)
  const [error, setError] = useState(false)
  const [loading, setLoading] = useState(false)

  async function handleSearch(e) {
    e.preventDefault()
    const trimmed = city.trim()
    if (!trimmed) return

    setLoading(true)
    setWeather(null)
    setError(false)

    const data = await getWeather(trimmed)

    setLoading(false)
    if (data.error) {
      setError(true)
    } else {
      setWeather(data)
    }
  }

  return (
    <div className={styles.page}>
      <Navbar />

      <main className={styles.main}>
        <div className={styles.hero}>
          <h1 className={styles.title}>Weather Lookup</h1>
          <p className={styles.subtitle}>
            Real-time weather conditions for any city worldwide.
          </p>
        </div>

        <div className={styles.card}>
          <form onSubmit={handleSearch} className={styles.form}>
            <input
              type="text"
              className={styles.input}
              placeholder="Enter city name"
              value={city}
              onChange={e => setCity(e.target.value)}
              maxLength={100}
              autoFocus
            />
            <button type="submit" className={styles.button} disabled={loading}>
              {loading ? 'Searching...' : 'Search'}
            </button>
          </form>

          {error && (
            <div className={styles.errorBox}>
              No results found for &ldquo;{city}&rdquo;. Check the spelling and try again.
            </div>
          )}

          {weather && (
            <div className={styles.result}>
              <div className={styles.resultHeader}>
                <div>
                  <h2 className={styles.cityName}>{weather.city}</h2>
                  <p className={styles.description}>{weather.description}</p>
                </div>
                <span className={styles.tempLarge}>{weather.temperature}&deg;C</span>
              </div>

              <div className={styles.statsGrid}>
                <div className={styles.stat}>
                  <span className={styles.statLabel}>Feels Like</span>
                  <span className={styles.statValue}>{weather.feelsLike}&deg;C</span>
                </div>
                <div className={styles.stat}>
                  <span className={styles.statLabel}>Humidity</span>
                  <span className={styles.statValue}>{weather.humidity}%</span>
                </div>
              </div>
            </div>
          )}
        </div>
      </main>
    </div>
  )
}
