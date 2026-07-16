// --------------------------------------------------
// STATE & CONFIGURATION
// --------------------------------------------------
let currentUser = null;

// Views Mapping based on User Role
const ROLE_VIEWS = {
    STUDENT: ['student-dashboard', 'student-jobs', 'student-applications', 'student-profile'],
    COMPANY: ['company-dashboard', 'company-post-job', 'company-applications', 'company-profile'],
    ADMIN: ['admin-dashboard', 'admin-students', 'admin-companies', 'admin-jobs']
};

// --------------------------------------------------
// DOCUMENT READY / ROUTING ENTRY
// --------------------------------------------------
document.addEventListener('DOMContentLoaded', () => {
    initEventListeners();
    checkExistingSession();
});

// Check if user is already authenticated
async function checkExistingSession() {
    try {
        const response = await fetch('/api/auth/me');
        if (response.ok) {
            currentUser = await response.json();
            setupPortalSession();
        } else {
            showAuthScreen();
        }
    } catch (e) {
        showAuthScreen();
    }
}

// --------------------------------------------------
// EVENT LISTENERS INITIALIZATION
// --------------------------------------------------
function initEventListeners() {
    // Auth Screen Toggles
    document.getElementById('switch-to-signup').addEventListener('click', toggleAuthForms);
    document.getElementById('switch-to-login').addEventListener('click', toggleAuthForms);

    // Signup Role Toggle
    const roleRadios = document.querySelectorAll('input[name="role"]');
    roleRadios.forEach(radio => {
        radio.addEventListener('change', (e) => {
            // Update Active tab styling
            document.querySelectorAll('.role-tab').forEach(tab => tab.classList.remove('active'));
            e.target.closest('.role-tab').classList.add('active');

            // Show appropriate fields
            if (e.target.value === 'STUDENT') {
                document.getElementById('student-fields').classList.remove('hidden');
                document.getElementById('company-fields').classList.add('hidden');
            } else {
                document.getElementById('student-fields').classList.add('hidden');
                document.getElementById('company-fields').classList.remove('hidden');
            }
        });
    });

    // Form Submissions
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('signup-form').addEventListener('submit', handleSignup);
    document.getElementById('student-profile-form').addEventListener('submit', handleStudentProfileUpdate);
    document.getElementById('company-profile-form').addEventListener('submit', handleCompanyProfileUpdate);
    document.getElementById('company-post-job-form').addEventListener('submit', handlePostJob);
    document.getElementById('evaluation-form').addEventListener('submit', handleEvaluationSubmit);

    // Logout Action
    document.getElementById('logout-button').addEventListener('click', handleLogout);

    // Sidebar Navigation Click Handling
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const viewName = link.getAttribute('data-view');
            switchView(viewName);
        });
    });

    // Job Search Filtering
    document.getElementById('job-search').addEventListener('input', (e) => {
        filterJobCards(e.target.value);
    });
}

// --------------------------------------------------
// VIEW NAVIGATION CONTROLLER
// --------------------------------------------------
function switchView(viewName) {
    // 1. Hide all views
    document.querySelectorAll('.portal-view').forEach(view => {
        view.classList.add('hidden');
        view.classList.remove('active');
    });

    // 2. Remove active class from all links
    document.querySelectorAll('.nav-link').forEach(link => link.classList.remove('active'));

    // 3. Show matching view
    const targetView = document.getElementById(`view-${viewName}`);
    if (targetView) {
        targetView.classList.remove('hidden');
        targetView.classList.add('active');
    }

    // 4. Highlight active navigation item
    const activeLink = document.querySelector(`.nav-link[data-view="${viewName}"]`);
    if (activeLink) {
        activeLink.classList.add('active');
    }

    // 5. Update header title
    const formattedTitle = viewName
        .split('-')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
    document.getElementById('view-title').innerText = formattedTitle;

    // 6. Trigger data fetch based on view
    triggerViewDataFetch(viewName);
}

function triggerViewDataFetch(viewName) {
    switch (viewName) {
        // Student Views
        case 'student-dashboard':
            loadStudentDashboardData();
            break;
        case 'student-jobs':
            loadStudentJobsGrid();
            break;
        case 'student-applications':
            loadStudentApplicationsTable();
            break;
        case 'student-profile':
            loadStudentProfileForm();
            break;

        // Company Views
        case 'company-dashboard':
            loadCompanyDashboardData();
            break;
        case 'company-applications':
            loadCompanyApplicationsTable();
            break;
        case 'company-profile':
            loadCompanyProfileForm();
            break;

        // Admin Views
        case 'admin-dashboard':
            loadAdminDashboardData();
            break;
        case 'admin-students':
            loadAdminStudentsTable();
            break;
        case 'admin-companies':
            loadAdminCompaniesTable();
            break;
        case 'admin-jobs':
            loadAdminJobsTable();
            break;
    }
}

// --------------------------------------------------
// AUTHENTICATION LOGIC (API ROUTINGS)
// --------------------------------------------------
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            currentUser = await response.json();
            showToast('Welcome back! Login successful.', 'success');
            setupPortalSession();
        } else {
            const errorText = await response.text();
            showToast(errorText || 'Authentication failed.', 'error');
        }
    } catch (err) {
        showToast('Server connection failed.', 'error');
    }
}

async function handleSignup(e) {
    e.preventDefault();
    const role = document.querySelector('input[name="role"]:checked').value;
    const signupData = {
        fullName: document.getElementById('signup-name').value,
        email: document.getElementById('signup-email').value,
        password: document.getElementById('signup-password').value,
        role: role
    };

    if (role === 'STUDENT') {
        signupData.cgpa = parseFloat(document.getElementById('signup-cgpa').value) || 0.0;
        signupData.branch = document.getElementById('signup-branch').value;
        signupData.skills = document.getElementById('signup-skills').value;
        signupData.resumeUrl = document.getElementById('signup-resume').value;
        signupData.graduationYear = parseInt(document.getElementById('signup-grad-year').value) || 2026;
    } else {
        signupData.companyName = document.getElementById('signup-company-name').value;
        signupData.industry = document.getElementById('signup-industry').value;
        signupData.website = document.getElementById('signup-website').value;
        signupData.description = document.getElementById('signup-company-desc').value;
    }

    try {
        const response = await fetch('/api/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(signupData)
        });

        if (response.ok) {
            showToast('Registration successful! Please login.', 'success');
            toggleAuthForms(); // Switch back to login form
        } else {
            const errorMsg = await response.text();
            showToast(errorMsg || 'Failed to register account.', 'error');
        }
    } catch (err) {
        showToast('Server connection failed.', 'error');
    }
}

async function handleLogout() {
    try {
        const response = await fetch('/api/auth/logout', { method: 'POST' });
        if (response.ok) {
            currentUser = null;
            showToast('Session ended. Goodbye!', 'success');
            showAuthScreen();
        }
    } catch (err) {
        showToast('Network error during sign-out.', 'error');
    }
}

// Setup User Interface after Login Success
function setupPortalSession() {
    document.getElementById('auth-container').classList.add('hidden');
    document.getElementById('portal-container').classList.remove('hidden');

    // Load User Header Information
    document.getElementById('nav-user-name').innerText = currentUser.fullName;
    document.getElementById('nav-user-role').innerText = currentUser.role;

    // Toggle Role-Specific Sidebar Navigation Links
    document.getElementById('nav-student-links').classList.add('hidden');
    document.getElementById('nav-company-links').classList.add('hidden');
    document.getElementById('nav-admin-links').classList.add('hidden');

    const roleLower = currentUser.role.toLowerCase();
    document.getElementById(`nav-${roleLower}-links`).classList.remove('hidden');

    // Switch view to the first page available for their role
    const defaultView = ROLE_VIEWS[currentUser.role][0];
    switchView(defaultView);
}

function showAuthScreen() {
    document.getElementById('auth-container').classList.remove('hidden');
    document.getElementById('portal-container').classList.add('hidden');
    
    // Clear forms
    document.getElementById('login-form').reset();
    document.getElementById('signup-form').reset();
}

function toggleAuthForms() {
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');
    const authTitle = document.getElementById('auth-title');
    const authSubtitle = document.getElementById('auth-subtitle');

    if (loginForm.classList.contains('hidden')) {
        loginForm.classList.remove('hidden');
        signupForm.classList.add('hidden');
        authTitle.innerText = "Welcome back";
        authSubtitle.innerText = "Log in to check eligibility, apply for jobs or manage recruitments.";
    } else {
        loginForm.classList.add('hidden');
        signupForm.classList.remove('hidden');
        authTitle.innerText = "Join the hub";
        authSubtitle.innerText = "Register your credentials to start seeking or publishing jobs.";
    }
}

// --------------------------------------------------
// STUDENT CONTROLLER DATA BINDINGS
// --------------------------------------------------
async function loadStudentDashboardData() {
    // 1. Set Name Greeting
    document.querySelectorAll('.student-name-placeholder').forEach(el => el.innerText = currentUser.fullName);
    
    try {
        // Refresh User Data
        const meRes = await fetch('/api/auth/me');
        if (meRes.ok) {
            currentUser = await meRes.json();
        }

        const profile = currentUser.studentProfile;
        if (!profile) return;

        // Set CGPA
        document.getElementById('student-stat-cgpa').innerText = profile.cgpa.toFixed(2);
        
        // Render Placement Status Badge
        const statusBadge = document.getElementById('student-status-badge');
        statusBadge.innerText = profile.placementStatus;
        if (profile.placementStatus === 'PLACED') {
            statusBadge.className = "badge badge-success";
        } else {
            statusBadge.className = "badge badge-warning";
        }

        // Fetch Submitted Applications
        const appsResponse = await fetch('/api/student/applications');
        if (appsResponse.ok) {
            const apps = await appsResponse.json();
            
            // Set Stats
            document.getElementById('student-stat-apps').innerText = apps.length;
            const shortlists = apps.filter(a => a.status === 'SHORTLISTED' || a.status === 'SELECTED').length;
            document.getElementById('student-stat-shortlists').innerText = shortlists;

            // Load recent 4 into dashboard table
            const recentAppsList = document.getElementById('student-recent-apps-list');
            recentAppsList.innerHTML = '';
            
            if (apps.length === 0) {
                recentAppsList.innerHTML = `<tr><td colspan="4" class="text-center">No applications submitted yet.</td></tr>`;
                return;
            }

            apps.slice(0, 4).forEach(app => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${app.jobPost.title}</strong></td>
                    <td>${app.jobPost.company.companyName}</td>
                    <td>${formatDate(app.appliedAt)}</td>
                    <td>${getBadgeHtml(app.status)}</td>
                `;
                recentAppsList.appendChild(tr);
            });
        }
    } catch (e) {
        showToast('Failed to load dashboard data.', 'error');
    }
}

async function loadStudentJobsGrid() {
    const grid = document.getElementById('student-jobs-grid');
    grid.innerHTML = '<div class="text-center" style="grid-column: 1/-1;">Loading jobs feeds...</div>';

    try {
        const response = await fetch('/api/student/jobs');
        if (response.ok) {
            const jobs = await response.json();
            grid.innerHTML = '';

            if (jobs.length === 0) {
                grid.innerHTML = '<div class="text-center" style="grid-column: 1/-1;">No active job postings found.</div>';
                return;
            }

            jobs.forEach(job => {
                const card = document.createElement('div');
                card.className = 'job-card';
                card.setAttribute('data-searchable', `${job.title} ${job.companyName} ${job.location}`.toLowerCase());

                // Apply Button status configuration
                let buttonHtml = '';
                if (job.applied) {
                    buttonHtml = `<button class="btn btn-secondary btn-sm" disabled><i class="fa-solid fa-check"></i> Applied</button>`;
                } else if (!job.eligible) {
                    buttonHtml = `<button class="btn btn-secondary btn-sm" disabled><i class="fa-solid fa-ban"></i> Ineligible</button>`;
                } else {
                    buttonHtml = `<button class="btn btn-primary btn-sm" onclick="applyForJob(${job.id})">Apply Now <i class="fa-solid fa-paper-plane"></i></button>`;
                }

                card.innerHTML = `
                    <div class="job-card-header">
                        <div>
                            <div class="job-company">${job.companyName}</div>
                            <h3 class="job-title">${job.title}</h3>
                        </div>
                        <span class="job-ctc-tag">${job.ctc.toFixed(1)} LPA</span>
                    </div>
                    <div class="job-details-row">
                        <span><i class="fa-solid fa-location-dot"></i> ${job.location}</span>
                        <span><i class="fa-solid fa-briefcase"></i> Full-time</span>
                    </div>
                    <p class="job-description">${job.description}</p>
                    <div class="job-criteria-list">
                        <span>Min CGPA: <strong>${job.eligibleCgpa.toFixed(2)}</strong></span>
                        <span>Eligible Branches: <strong>${job.eligibleBranches}</strong></span>
                    </div>
                    <div class="job-card-footer">
                        <span class="eligibility-info">
                            ${job.eligible 
                                ? '<span class="text-green"><i class="fa-solid fa-circle-check"></i> Eligible</span>' 
                                : '<span class="text-yellow"><i class="fa-solid fa-circle-xmark"></i> Criteria unmet</span>'
                            }
                        </span>
                        ${buttonHtml}
                    </div>
                `;
                grid.appendChild(card);
            });
        }
    } catch (e) {
        grid.innerHTML = '<div class="text-center" style="grid-column: 1/-1;">Error loading jobs.</div>';
    }
}

async function applyForJob(jobId) {
    try {
        const response = await fetch(`/api/student/apply?jobId=${jobId}`, { method: 'POST' });
        if (response.ok) {
            showToast('Application submitted successfully!', 'success');
            loadStudentJobsGrid(); // reload grid
        } else {
            const err = await response.text();
            showToast(err || 'Failed to submit application.', 'error');
        }
    } catch (e) {
        showToast('Network error during application.', 'error');
    }
}

async function loadStudentApplicationsTable() {
    const tbody = document.getElementById('student-full-apps-list');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center">Loading applications list...</td></tr>';

    try {
        const response = await fetch('/api/student/applications');
        if (response.ok) {
            const apps = await response.json();
            tbody.innerHTML = '';

            if (apps.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center">No applications found. Find jobs and apply!</td></tr>';
                return;
            }

            apps.forEach(app => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${app.jobPost.title}</strong></td>
                    <td>${app.jobPost.company.companyName}</td>
                    <td>${app.jobPost.ctc.toFixed(1)} LPA</td>
                    <td>${formatDate(app.appliedAt)}</td>
                    <td>${getBadgeHtml(app.status)}</td>
                    <td><span class="text-muted">${app.feedback || 'Pending evaluation reviews'}</span></td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">Error reading applications.</td></tr>';
    }
}

function loadStudentProfileForm() {
    const profile = currentUser.studentProfile;
    if (!profile) return;

    document.getElementById('profile-cgpa').value = profile.cgpa;
    document.getElementById('profile-branch').value = profile.branch;
    document.getElementById('profile-skills').value = profile.skills || '';
    document.getElementById('profile-resume').value = profile.resumeUrl || '';
    document.getElementById('profile-grad-year').value = profile.graduationYear;
}

async function handleStudentProfileUpdate(e) {
    e.preventDefault();
    const updatedData = {
        cgpa: parseFloat(document.getElementById('profile-cgpa').value) || 0.0,
        branch: document.getElementById('profile-branch').value,
        skills: document.getElementById('profile-skills').value,
        resumeUrl: document.getElementById('profile-resume').value,
        graduationYear: parseInt(document.getElementById('profile-grad-year').value) || 2026
    };

    try {
        const response = await fetch('/api/student/profile', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            showToast('Academic profile updated successfully!', 'success');
            // Refresh currentUser state
            const meRes = await fetch('/api/auth/me');
            if (meRes.ok) currentUser = await meRes.json();
        } else {
            const err = await response.text();
            showToast(err || 'Failed to update profile.', 'error');
        }
    } catch (e) {
        showToast('Network connection failed.', 'error');
    }
}

function filterJobCards(query) {
    const cleanQuery = query.toLowerCase().trim();
    const cards = document.querySelectorAll('#student-jobs-grid .job-card');
    
    cards.forEach(card => {
        const searchableText = card.getAttribute('data-searchable');
        if (searchableText.includes(cleanQuery)) {
            card.classList.remove('hidden');
        } else {
            card.classList.add('hidden');
        }
    });
}

// --------------------------------------------------
// COMPANY CONTROLLER DATA BINDINGS
// --------------------------------------------------
async function loadCompanyDashboardData() {
    const profile = currentUser.companyProfile;
    if (!profile) return;

    document.querySelectorAll('.company-name-placeholder').forEach(el => el.innerText = profile.companyName);

    try {
        // Fetch Jobs
        const jobsRes = await fetch('/api/company/jobs');
        if (jobsRes.ok) {
            const jobs = await jobsRes.json();
            document.getElementById('company-stat-jobs').innerText = jobs.length;

            const tbody = document.getElementById('company-active-jobs-list');
            tbody.innerHTML = '';

            if (jobs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center">No jobs published. Create one under "Post a Job"!</td></tr>';
            } else {
                jobs.forEach(job => {
                    const tr = document.createElement('tr');
                    
                    // Count apps matching this job from global apps list
                    tr.innerHTML = `
                        <td><strong>${job.title}</strong></td>
                        <td>${job.location}</td>
                        <td>${job.ctc.toFixed(1)} LPA</td>
                        <td>${job.eligibleCgpa.toFixed(2)}</td>
                        <td>${job.eligibleBranches}</td>
                        <td><span class="badge badge-info" id="job-app-count-${job.id}">Loading</span></td>
                        <td>
                            <label class="switch-toggle">
                                <input type="checkbox" ${job.status === 'OPEN' ? 'checked' : ''} onchange="toggleJobStatus(${job.id}, this)">
                                <span class="badge ${job.status === 'OPEN' ? 'badge-success' : 'badge-danger'}">${job.status}</span>
                            </label>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        }

        // Fetch Applications
        const appsRes = await fetch('/api/company/applications');
        if (appsRes.ok) {
            const apps = await appsRes.json();
            document.getElementById('company-stat-apps').innerText = apps.length;

            const selectedCount = apps.filter(a => a.status === 'SELECTED').length;
            document.getElementById('company-stat-selected').innerText = selectedCount;

            // Set app counts for each job profile
            const activeJobs = document.querySelectorAll('[id^="job-app-count-"]');
            activeJobs.forEach(jobBadge => {
                const jobId = parseInt(jobBadge.id.split('-').pop());
                const count = apps.filter(a => a.jobPost.id === jobId).length;
                jobBadge.innerText = `${count} Applied`;
            });
        }
    } catch (e) {
        showToast('Error loading company dashboard.', 'error');
    }
}

async function toggleJobStatus(jobId, checkbox) {
    const newStatus = checkbox.checked ? 'OPEN' : 'CLOSED';
    try {
        const response = await fetch(`/api/company/jobs/${jobId}/status?status=${newStatus}`, { method: 'PUT' });
        if (response.ok) {
            showToast(`Job listing is now ${newStatus}!`, 'success');
            loadCompanyDashboardData();
        } else {
            checkbox.checked = !checkbox.checked; // revert
            showToast('Failed to change status.', 'error');
        }
    } catch (e) {
        checkbox.checked = !checkbox.checked; // revert
        showToast('Network error.', 'error');
    }
}

async function handlePostJob(e) {
    e.preventDefault();
    const jobData = {
        title: document.getElementById('job-title').value,
        location: document.getElementById('job-location').value,
        ctc: parseFloat(document.getElementById('job-ctc').value) || 0.0,
        eligibleCgpa: parseFloat(document.getElementById('job-cutoff').value) || 0.0,
        eligibleBranches: document.getElementById('job-branches').value
    };
    jobData.description = document.getElementById('job-desc').value;

    try {
        const response = await fetch('/api/company/jobs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(jobData)
        });

        if (response.ok) {
            showToast('Job opening published successfully!', 'success');
            document.getElementById('company-post-job-form').reset();
            switchView('company-dashboard');
        } else {
            const err = await response.text();
            showToast(err || 'Failed to post job.', 'error');
        }
    } catch (e) {
        showToast('Network connection failed.', 'error');
    }
}

async function loadCompanyApplicationsTable() {
    const tbody = document.getElementById('company-apps-list');
    tbody.innerHTML = '<tr><td colspan="7" class="text-center">Loading applications directory...</td></tr>';

    try {
        const response = await fetch('/api/company/applications');
        if (response.ok) {
            const apps = await response.json();
            tbody.innerHTML = '';

            if (apps.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center">No student applications received yet.</td></tr>';
                return;
            }

            apps.forEach(app => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${app.student.user.fullName}</strong><br><span class="text-muted" style="font-size:11px;">${app.student.user.email}</span></td>
                    <td>CGPA: <strong>${app.student.cgpa.toFixed(2)}</strong><br><span class="text-muted">${app.student.branch} | Grad ${app.student.graduationYear}</span></td>
                    <td><span style="font-size:12px;">${app.student.skills || 'Not specified'}</span></td>
                    <td><strong>${app.jobPost.title}</strong></td>
                    <td><a href="${app.student.resumeUrl}" target="_blank" class="h2-badge"><i class="fa-solid fa-file-pdf"></i> Resume</a></td>
                    <td>${getBadgeHtml(app.status)}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="openReviewModal(${app.id}, '${app.student.user.fullName}', '${app.jobPost.title}', '${app.status}')">
                            <i class="fa-solid fa-user-pen"></i> Review
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">Error reading applications database.</td></tr>';
    }
}

function loadCompanyProfileForm() {
    const profile = currentUser.companyProfile;
    if (!profile) return;

    document.getElementById('comp-profile-name').value = profile.companyName;
    document.getElementById('comp-profile-industry').value = profile.industry;
    document.getElementById('comp-profile-website').value = profile.website || '';
    document.getElementById('comp-profile-desc').value = profile.description || '';
}

async function handleCompanyProfileUpdate(e) {
    e.preventDefault();
    const updatedData = {
        companyName: document.getElementById('comp-profile-name').value,
        industry: document.getElementById('comp-profile-industry').value,
        website: document.getElementById('comp-profile-website').value,
        description: document.getElementById('comp-profile-desc').value
    };

    try {
        const response = await fetch('/api/company/profile', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedData)
        });

        if (response.ok) {
            showToast('Company details saved successfully!', 'success');
            // Refresh state
            const meRes = await fetch('/api/auth/me');
            if (meRes.ok) currentUser = await meRes.json();
        } else {
            const err = await response.text();
            showToast(err || 'Failed to save company profile.', 'error');
        }
    } catch (e) {
        showToast('Network error.', 'error');
    }
}

// --------------------------------------------------
// CANDIDATE EVALUATION MODAL CONTROLS
// --------------------------------------------------
function openReviewModal(appId, studentName, jobTitle, currentStatus) {
    document.getElementById('eval-app-id').value = appId;
    document.getElementById('eval-candidate-name').value = studentName;
    document.getElementById('eval-job-title').value = jobTitle;
    document.getElementById('eval-decision').value = currentStatus === 'APPLIED' ? 'SHORTLISTED' : currentStatus;
    document.getElementById('eval-feedback').value = '';
    
    document.getElementById('review-modal').classList.remove('hidden');
}

function closeReviewModal() {
    document.getElementById('review-modal').classList.add('hidden');
}

async function handleEvaluationSubmit(e) {
    e.preventDefault();
    const appId = document.getElementById('eval-app-id').value;
    const status = document.getElementById('eval-decision').value;
    const feedback = document.getElementById('eval-feedback').value;

    try {
        const response = await fetch(`/api/company/applications/${appId}/status?status=${status}&feedback=${encodeURIComponent(feedback)}`, {
            method: 'PUT'
        });

        if (response.ok) {
            showToast('Candidate evaluation updated successfully!', 'success');
            closeReviewModal();
            loadCompanyApplicationsTable();
        } else {
            showToast('Failed to update evaluation.', 'error');
        }
    } catch (e) {
        showToast('Network connection failed.', 'error');
    }
}

// --------------------------------------------------
// ADMIN CONTROLLER DATA BINDINGS
// --------------------------------------------------
async function loadAdminDashboardData() {
    try {
        const response = await fetch('/api/admin/stats');
        if (response.ok) {
            const stats = await response.json();

            // Populate dashboard labels
            document.getElementById('admin-stat-students').innerText = stats.totalStudents;
            document.getElementById('admin-stat-placements').innerText = `${stats.placementPercentage}%`;
            document.getElementById('admin-stat-jobs').innerText = stats.openJobs;
            document.getElementById('admin-stat-avg-ctc').innerText = `${stats.averageCtc.toFixed(1)} LPA`;

            // Draw CSS Conic Gradient Chart
            const chartCircle = document.getElementById('placement-chart-circle');
            chartCircle.style.background = `conic-gradient(var(--color-green) 0% ${stats.placementPercentage}%, rgba(255, 255, 255, 0.05) ${stats.placementPercentage}% 100%)`;
            
            document.getElementById('chart-percent-val').innerText = `${stats.placementPercentage}%`;
            document.getElementById('chart-placed-count').innerText = stats.placedStudents;
            document.getElementById('chart-unplaced-count').innerText = stats.unplacedStudents;
        }
    } catch (e) {
        showToast('Failed to load admin statistics.', 'error');
    }
}

async function loadAdminStudentsTable() {
    const tbody = document.getElementById('admin-students-list');
    tbody.innerHTML = '<tr><td colspan="9" class="text-center">Loading database...</td></tr>';

    try {
        const response = await fetch('/api/admin/users');
        if (response.ok) {
            const data = await response.json();
            tbody.innerHTML = '';

            const students = data.students;
            if (students.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="text-center">No students registered in database.</td></tr>';
                return;
            }

            students.forEach(std => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>STD-${std.id}</strong></td>
                    <td>${std.user.fullName}</td>
                    <td>${std.user.email}</td>
                    <td><strong>${std.cgpa.toFixed(2)}</strong></td>
                    <td>${std.branch}</td>
                    <td>${std.graduationYear}</td>
                    <td><span style="font-size:12px;">${std.skills || 'None'}</span></td>
                    <td><a href="${std.resumeUrl}" target="_blank" class="h2-badge"><i class="fa-solid fa-file-pdf"></i> View</a></td>
                    <td>${std.placementStatus === 'PLACED' 
                        ? '<span class="badge badge-success">PLACED</span>' 
                        : '<span class="badge badge-warning">UNPLACED</span>'
                    }</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">Error reading student records.</td></tr>';
    }
}

async function loadAdminCompaniesTable() {
    const tbody = document.getElementById('admin-companies-list');
    tbody.innerHTML = '<tr><td colspan="5" class="text-center">Loading companies list...</td></tr>';

    try {
        const response = await fetch('/api/admin/users');
        if (response.ok) {
            const data = await response.json();
            tbody.innerHTML = '';

            const companies = data.companies;
            if (companies.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center">No companies registered in system.</td></tr>';
                return;
            }

            companies.forEach(cp => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>${cp.companyName}</strong></td>
                    <td>${cp.industry}</td>
                    <td><a href="${cp.website}" target="_blank">${cp.website}</a></td>
                    <td><p class="job-description" style="-webkit-line-clamp: 2;">${cp.description}</p></td>
                    <td>${cp.user.email}</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center">Error loading companies directory.</td></tr>';
    }
}

async function loadAdminJobsTable() {
    const tbody = document.getElementById('admin-jobs-list');
    tbody.innerHTML = '<tr><td colspan="9" class="text-center">Loading postings...</td></tr>';

    try {
        const response = await fetch('/api/admin/jobs');
        if (response.ok) {
            const jobs = await response.json();
            tbody.innerHTML = '';

            if (jobs.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="text-center">No job posts created in database.</td></tr>';
                return;
            }

            jobs.forEach(job => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><strong>JOB-${job.id}</strong></td>
                    <td><strong>${job.title}</strong></td>
                    <td>${job.company.companyName}</td>
                    <td>${job.location}</td>
                    <td>${job.ctc.toFixed(1)} LPA</td>
                    <td>${job.eligibleCgpa.toFixed(2)}</td>
                    <td>${job.eligibleBranches}</td>
                    <td>${job.status === 'OPEN' 
                        ? '<span class="badge badge-success">OPEN</span>' 
                        : '<span class="badge badge-danger">CLOSED</span>'
                    }</td>
                    <td>
                        <button class="btn btn-sm btn-secondary" onclick="adminToggleJob(${job.id}, '${job.status}')">
                            <i class="fa-solid fa-power-off"></i> Toggle
                        </button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (e) {
        tbody.innerHTML = '<tr><td colspan="9" class="text-center">Error reading jobs database.</td></tr>';
    }
}

async function adminToggleJob(jobId, currentStatus) {
    const newStatus = currentStatus === 'OPEN' ? 'CLOSED' : 'OPEN';
    try {
        const response = await fetch(`/api/admin/jobs/${jobId}/status?status=${newStatus}`, { method: 'PUT' });
        if (response.ok) {
            showToast(`Job listing S-${jobId} toggled to ${newStatus}`, 'success');
            loadAdminJobsTable();
        } else {
            showToast('Authorization or request failed.', 'error');
        }
    } catch (e) {
        showToast('Server update error.', 'error');
    }
}

// --------------------------------------------------
// GENERAL HELPERS
// --------------------------------------------------
function formatDate(dateString) {
    if (!dateString) return 'Pending';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        day: '2-digit',
        month: 'short',
        year: 'numeric'
    });
}

function getBadgeHtml(status) {
    switch (status) {
        case 'APPLIED':
            return `<span class="badge badge-warning"><i class="fa-solid fa-clock"></i> Applied</span>`;
        case 'SHORTLISTED':
            return `<span class="badge badge-info"><i class="fa-solid fa-user-clock"></i> Shortlisted</span>`;
        case 'SELECTED':
            return `<span class="badge badge-success"><i class="fa-solid fa-square-check"></i> Selected</span>`;
        case 'REJECTED':
            return `<span class="badge badge-danger"><i class="fa-solid fa-ban"></i> Rejected</span>`;
        default:
            return `<span class="badge badge-secondary">${status}</span>`;
    }
}

// Toast notification alert system
function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const icon = type === 'success' ? 'fa-circle-check text-green' : 'fa-triangle-exclamation text-red';
    toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;
    
    container.appendChild(toast);
    
    // Automatically fade out and delete toast after 4s
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-10px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
