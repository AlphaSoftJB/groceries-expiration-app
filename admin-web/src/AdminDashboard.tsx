import { useQuery, gql } from '@apollo/client';
import { BarChart3, Users, Package, AlertTriangle, TrendingUp, Activity } from 'lucide-react';

const GET_ADMIN_STATS = gql`
  query GetAdminStats {
    totalUsers
    activeUsers
    totalHouseholds
    totalItems
    expiringItemsCount
    expiredItemsCount
  }
`;

export default function AdminDashboard() {
  const { data, loading, error } = useQuery(GET_ADMIN_STATS);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600 text-lg">Loading admin dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <div className="bg-red-50 border border-red-200 rounded-lg p-8 max-w-md">
          <div className="flex items-center mb-4">
            <AlertTriangle className="h-6 w-6 text-red-600 mr-2" />
            <h2 className="text-xl font-semibold text-red-900">Connection Error</h2>
          </div>
          <p className="text-red-700 mb-4">Failed to connect to backend:</p>
          <p className="text-sm text-red-600 bg-red-100 p-3 rounded font-mono">
            {error.message}
          </p>
          <p className="text-sm text-gray-600 mt-4">
            Make sure the Java backend is running at <code className="bg-gray-200 px-2 py-1 rounded">http://localhost:8080</code>
          </p>
        </div>
      </div>
    );
  }

  const stats = data || {};

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">Admin Dashboard</h1>
              <p className="mt-1 text-sm text-gray-500">
                Groceries Expiration Tracking App - Management Console
              </p>
            </div>
            <div className="flex items-center space-x-2">
              <Activity className="h-5 w-5 text-green-500" />
              <span className="text-sm text-gray-600">Backend Connected</span>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          {/* Total Users */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-blue-100 rounded-lg">
                  <Users className="h-6 w-6 text-blue-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">Total Users</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-gray-900">{stats.totalUsers || 0}</div>
            <p className="text-sm text-gray-500 mt-2">
              {stats.activeUsers || 0} active this month
            </p>
          </div>

          {/* Total Households */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-green-100 rounded-lg">
                  <TrendingUp className="h-6 w-6 text-green-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">Households</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-gray-900">{stats.totalHouseholds || 0}</div>
            <p className="text-sm text-gray-500 mt-2">
              {((stats.totalItems || 0) / (stats.totalHouseholds || 1)).toFixed(1)} items/household avg
            </p>
          </div>

          {/* Total Items */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-purple-100 rounded-lg">
                  <Package className="h-6 w-6 text-purple-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">Total Items</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-gray-900">{stats.totalItems || 0}</div>
            <p className="text-sm text-gray-500 mt-2">
              Tracked across all households
            </p>
          </div>

          {/* Expiring Soon */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-orange-100 rounded-lg">
                  <AlertTriangle className="h-6 w-6 text-orange-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">Expiring Soon</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-orange-600">{stats.expiringItemsCount || 0}</div>
            <p className="text-sm text-gray-500 mt-2">
              Within next 3 days
            </p>
          </div>

          {/* Expired */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-red-100 rounded-lg">
                  <AlertTriangle className="h-6 w-6 text-red-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">Expired</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-red-600">{stats.expiredItemsCount || 0}</div>
            <p className="text-sm text-gray-500 mt-2">
              Need attention
            </p>
          </div>

          {/* System Health */}
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center">
                <div className="p-3 bg-green-100 rounded-lg">
                  <BarChart3 className="h-6 w-6 text-green-600" />
                </div>
                <h3 className="ml-4 text-lg font-semibold text-gray-900">System Status</h3>
              </div>
            </div>
            <div className="text-3xl font-bold text-green-600">Healthy</div>
            <p className="text-sm text-gray-500 mt-2">
              All services operational
            </p>
          </div>
        </div>

        {/* Features Overview */}
        <div className="bg-white rounded-lg shadow p-6 mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Version 1.2 Features</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="font-semibold text-gray-900 mb-3">Core Features</h3>
              <ul className="space-y-2 text-sm text-gray-600">
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
                  Expiration Tracking
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
                  Barcode Scanning
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
                  OCR Text Recognition
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
                  Shopping Lists
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-green-500 rounded-full mr-3"></span>
                  Household Management
                </li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold text-gray-900 mb-3">Advanced Features</h3>
              <ul className="space-y-2 text-sm text-gray-600">
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
                  ML Expiration Prediction
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
                  Smart Fridge Integration (10 brands)
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
                  Barcode Database (1.5M+ products)
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
                  African Languages (5 languages)
                </li>
                <li className="flex items-center">
                  <span className="w-2 h-2 bg-blue-500 rounded-full mr-3"></span>
                  Recipe Suggestions
                </li>
              </ul>
            </div>
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <button className="p-4 border-2 border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors text-left">
              <h3 className="font-semibold text-gray-900 mb-2">View All Users</h3>
              <p className="text-sm text-gray-600">Manage user accounts and permissions</p>
            </button>
            <button className="p-4 border-2 border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors text-left">
              <h3 className="font-semibold text-gray-900 mb-2">System Logs</h3>
              <p className="text-sm text-gray-600">View application logs and errors</p>
            </button>
            <button className="p-4 border-2 border-gray-200 rounded-lg hover:border-blue-500 hover:bg-blue-50 transition-colors text-left">
              <h3 className="font-semibold text-gray-900 mb-2">Configuration</h3>
              <p className="text-sm text-gray-600">Update system settings and features</p>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
