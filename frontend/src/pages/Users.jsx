import { useEffect, useState } from 'react'
import Navbar from '../components/Navbar'
import { getUsers, deleteUser } from '../api'
import styles from './Users.module.css'

export default function Users() {
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [deletingId, setDeletingId] = useState(null)

  async function loadUsers() {
    setLoading(true)
    const data = await getUsers()
    setUsers(data)
    setLoading(false)
  }

  async function handleDelete(id) {
    if (!confirm('Delete this user? This action cannot be undone.')) return

    setDeletingId(id)
    const data = await deleteUser(id)
    setDeletingId(null)

    if (data.success) {
      setUsers(prev => prev.filter(u => u._id !== id))
    } else {
      alert('Failed to delete user.')
    }
  }

  useEffect(() => {
    loadUsers()
  }, [])

  return (
    <div className={styles.page}>
      <Navbar admin />

      <main className={styles.main}>
        <div className={styles.pageHeader}>
          <div>
            <h1 className={styles.pageTitle}>Users</h1>
            <p className={styles.pageDesc}>
              {loading ? 'Loading...' : `${users.length} registered user${users.length !== 1 ? 's' : ''}`}
            </p>
          </div>
        </div>

        <div className={styles.tableCard}>
          {loading ? (
            <div className={styles.emptyState}>Loading users...</div>
          ) : users.length === 0 ? (
            <div className={styles.emptyState}>No users found.</div>
          ) : (
            <table className={styles.table}>
              <thead>
                <tr>
                  <th className={styles.th}>#</th>
                  <th className={styles.th}>Name</th>
                  <th className={styles.th}>City</th>
                  <th className={styles.th}>Address</th>
                  <th className={styles.th}></th>
                </tr>
              </thead>
              <tbody>
                {users.map((user, index) => (
                  <tr key={user._id} className={styles.row}>
                    <td className={`${styles.td} ${styles.num}`}>{index + 1}</td>
                    <td className={styles.td}>{user.name}</td>
                    <td className={styles.td}>{user.city}</td>
                    <td className={styles.td}>{user.address}</td>
                    <td className={`${styles.td} ${styles.actionCell}`}>
                      <button
                        className={styles.deleteBtn}
                        onClick={() => handleDelete(user._id)}
                        disabled={deletingId === user._id}
                      >
                        {deletingId === user._id ? 'Deleting...' : 'Delete'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
    </div>
  )
}
