/**
 * SkillWallet Capstone: AI-Powered Customer Support Ticketing System
 * Interactive Frontend Controller (Vanilla JavaScript ES6+)
 */

const API_BASE = '/api';

class TicketingApp {
  constructor() {
    this.currentView = 'dashboardView';
    this.tickets = [];
    this.currentTicket = null;
    this.charts = {
      status: null,
      priority: null,
      category: null
    };
    this.searchTimeout = null;
    this.draggedTicketId = null;

    this.init();
  }

  async init() {
    this.bindEvents();
    await this.checkAiStatus();
    await this.refreshAllData();
  }

  // -----------------------------------------------------------------
  // Event Bindings
  // -----------------------------------------------------------------
  bindEvents() {
    // Navigation Tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
      btn.addEventListener('click', (e) => {
        const viewId = e.currentTarget.getAttribute('data-view');
        this.switchView(viewId);
      });
    });

    // Create New Ticket Modal
    const openModalBtn = document.getElementById('openNewTicketModalBtn');
    if (openModalBtn) {
      openModalBtn.addEventListener('click', () => this.openNewTicketModal());
    }

    // Refresh Kanban
    const refreshKanbanBtn = document.getElementById('refreshKanbanBtn');
    if (refreshKanbanBtn) {
      refreshKanbanBtn.addEventListener('click', () => this.loadKanban());
    }

    // Search and Filters
    const searchInput = document.getElementById('searchInput');
    const clearSearchBtn = document.getElementById('clearSearchBtn');
    if (searchInput) {
      searchInput.addEventListener('input', (e) => {
        clearTimeout(this.searchTimeout);
        if (e.target.value.trim().length > 0) {
          clearSearchBtn.style.display = 'block';
        } else {
          clearSearchBtn.style.display = 'none';
        }
        this.searchTimeout = setTimeout(() => this.loadTicketsList(), 300);
      });
    }

    if (clearSearchBtn) {
      clearSearchBtn.addEventListener('click', () => {
        searchInput.value = '';
        clearSearchBtn.style.display = 'none';
        this.loadTicketsList();
      });
    }

    ['filterStatus', 'filterPriority', 'filterCategory', 'filterSort'].forEach(id => {
      const el = document.getElementById(id);
      if (el) {
        el.addEventListener('change', () => this.loadTicketsList());
      }
    });

    // Close modal on escape key
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.closeModal('newTicketModal');
        this.closeModal('ticketDetailModal');
      }
    });
  }

  // -----------------------------------------------------------------
  // Navigation & View Switching
  // -----------------------------------------------------------------
  switchView(viewId) {
    this.currentView = viewId;

    document.querySelectorAll('.tab-btn').forEach(btn => {
      const active = btn.getAttribute('data-view') === viewId;
      btn.classList.toggle('active', active);
      btn.setAttribute('aria-selected', active);
    });

    document.querySelectorAll('.view-panel').forEach(panel => {
      panel.classList.toggle('active', panel.id === viewId);
    });

    if (viewId === 'dashboardView') {
      this.loadDashboard();
    } else if (viewId === 'kanbanView') {
      this.loadKanban();
    } else if (viewId === 'listView') {
      this.loadTicketsList();
    }
  }

  async refreshAllData() {
    await Promise.all([
      this.loadDashboard(),
      this.loadKanban(),
      this.loadTicketsList()
    ]);
  }

  // -----------------------------------------------------------------
  // AI Status Check
  // -----------------------------------------------------------------
  async checkAiStatus() {
    const badge = document.getElementById('aiStatusBadge');
    const text = document.getElementById('aiStatusText');
    try {
      const res = await fetch(`${API_BASE}/ai/status`);
      if (!res.ok) throw new Error('AI status unreachable');
      const data = await res.json();

      badge.className = 'ai-status-badge ' + (data.liveApiConfigured ? 'ai-status-live' : 'ai-status-demo');
      text.textContent = data.liveApiConfigured
        ? 'AI Mode: Google Gemini Live'
        : 'AI Mode: Rule-Based Demo (Offline Ready)';
      badge.setAttribute('title', data.modeDescription);
    } catch (err) {
      badge.className = 'ai-status-badge ai-status-demo';
      text.textContent = 'AI Mode: Demo Engine (Active)';
    }
  }

  // -----------------------------------------------------------------
  // Dashboard & Metrics
  // -----------------------------------------------------------------
  async loadDashboard() {
    try {
      const res = await fetch(`${API_BASE}/dashboard/stats`);
      if (!res.ok) throw new Error('Failed to load dashboard metrics');
      const stats = await res.json();

      document.getElementById('metricTotal').textContent = stats.totalTickets;
      document.getElementById('metricOpen').textContent = stats.openTickets;
      document.getElementById('metricPending').textContent = stats.pendingTickets;
      document.getElementById('metricResolved').textContent = (stats.resolvedTickets + stats.closedTickets);
      document.getElementById('metricCritical').textContent = stats.criticalTickets;

      this.renderCharts(stats);
    } catch (err) {
      console.error('Dashboard loading error:', err);
      this.showToast('Failed to refresh dashboard stats', 'error');
    }
  }

  renderCharts(stats) {
    if (typeof Chart === 'undefined') {
      console.warn('Chart.js not loaded');
      return;
    }

    // 1. Status Donut Chart
    const statusCtx = document.getElementById('statusChart');
    if (statusCtx) {
      const labels = ['New', 'In Progress', 'Waiting for Customer', 'Resolved', 'Closed'];
      const data = [
        stats.statusBreakdown['NEW'] || 0,
        stats.statusBreakdown['IN_PROGRESS'] || 0,
        stats.statusBreakdown['WAITING_FOR_CUSTOMER'] || 0,
        stats.statusBreakdown['RESOLVED'] || 0,
        stats.statusBreakdown['CLOSED'] || 0
      ];

      if (this.charts.status) this.charts.status.destroy();
      this.charts.status = new Chart(statusCtx, {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [{
            data: data,
            backgroundColor: ['#2563eb', '#d97706', '#9333ea', '#16a34a', '#64748b'],
            borderWidth: 2,
            borderColor: '#ffffff'
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: { position: 'bottom', labels: { boxWidth: 12, font: { size: 11 } } }
          }
        }
      });
    }

    // 2. Priority Bar Chart
    const prioCtx = document.getElementById('priorityChart');
    if (prioCtx) {
      const prioLabels = ['Low', 'Medium', 'High', 'Critical'];
      const prioData = [
        stats.priorityBreakdown['LOW'] || 0,
        stats.priorityBreakdown['MEDIUM'] || 0,
        stats.priorityBreakdown['HIGH'] || 0,
        stats.priorityBreakdown['CRITICAL'] || 0
      ];

      if (this.charts.priority) this.charts.priority.destroy();
      this.charts.priority = new Chart(prioCtx, {
        type: 'bar',
        data: {
          labels: prioLabels,
          datasets: [{
            label: 'Ticket Count',
            data: prioData,
            backgroundColor: ['#10b981', '#3b82f6', '#f97316', '#ef4444'],
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            y: { beginAtZero: true, ticks: { precision: 0 } }
          }
        }
      });
    }

    // 3. Category Horizontal Bar Chart
    const catCtx = document.getElementById('categoryChart');
    if (catCtx) {
      const catLabels = [
        'Technical Support',
        'Billing & Invoices',
        'Account & Login',
        'Feature Request',
        'General Inquiry'
      ];
      const catData = [
        stats.categoryBreakdown['TECHNICAL'] || 0,
        stats.categoryBreakdown['BILLING'] || 0,
        stats.categoryBreakdown['ACCOUNT_ACCESS'] || 0,
        stats.categoryBreakdown['FEATURE_REQUEST'] || 0,
        stats.categoryBreakdown['GENERAL_INQUIRY'] || 0
      ];

      if (this.charts.category) this.charts.category.destroy();
      this.charts.category = new Chart(catCtx, {
        type: 'bar',
        data: {
          labels: catLabels,
          datasets: [{
            label: 'Volume',
            data: catData,
            backgroundColor: '#6366f1',
            borderRadius: 6
          }]
        },
        options: {
          indexAxis: 'y',
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: false } },
          scales: {
            x: { beginAtZero: true, ticks: { precision: 0 } }
          }
        }
      });
    }
  }

  // -----------------------------------------------------------------
  // Kanban Board Workflow
  // -----------------------------------------------------------------
  async loadKanban() {
    try {
      const res = await fetch(`${API_BASE}/tickets`);
      if (!res.ok) throw new Error('Failed to load tickets for Kanban');
      const tickets = await res.json();
      this.tickets = tickets;

      const statuses = ['NEW', 'IN_PROGRESS', 'WAITING_FOR_CUSTOMER', 'RESOLVED', 'CLOSED'];
      const buckets = { NEW: [], IN_PROGRESS: [], WAITING_FOR_CUSTOMER: [], RESOLVED: [], CLOSED: [] };

      tickets.forEach(t => {
        if (buckets[t.status]) {
          buckets[t.status].push(t);
        }
      });

      statuses.forEach(status => {
        const dropzone = document.getElementById(`kanban-${status}`);
        const countBadge = document.getElementById(`count-${status}`);
        if (!dropzone) return;

        countBadge.textContent = buckets[status].length;
        dropzone.innerHTML = '';

        if (buckets[status].length === 0) {
          dropzone.innerHTML = '<div style="color:#94a3b8; font-size:0.75rem; text-align:center; padding:1.5rem 0;">No tickets</div>';
          return;
        }

        buckets[status].forEach(ticket => {
          const card = document.createElement('div');
          card.className = 'kanban-card';
          card.draggable = true;
          card.setAttribute('data-id', ticket.id);
          card.ondragstart = (e) => this.handleDragStart(e, ticket.id);

          const prioClass = `priority-${ticket.priority.toLowerCase()}`;
          const nextStatus = this.getNextStatus(ticket.status);

          card.innerHTML = `
            <div class="card-top">
              <span class="ticket-tag">${this.escapeHtml(ticket.ticketNumber)}</span>
              <span class="badge ${prioClass}">${ticket.priority}</span>
            </div>
            <div class="card-subject">${this.escapeHtml(ticket.subject)}</div>
            <div class="card-meta">
              <span>👤 ${this.escapeHtml(ticket.customerName.split(' ')[0])}</span>
              <span>🛠️ ${this.escapeHtml(ticket.assignedAgent || 'Unassigned')}</span>
            </div>
            <div class="card-quick-actions">
              <button class="btn btn-secondary btn-sm flex-1" onclick="app.openTicketDetailModal(${ticket.id})">Details</button>
              ${nextStatus ? `<button class="btn btn-primary btn-sm" title="Advance to ${nextStatus.displayName}" onclick="app.quickAdvanceStatus(${ticket.id}, '${nextStatus.code}')">→</button>` : ''}
            </div>
          `;
          dropzone.appendChild(card);
        });
      });
    } catch (err) {
      console.error('Kanban load error:', err);
    }
  }

  getNextStatus(current) {
    const sequence = [
      { code: 'NEW', displayName: 'New' },
      { code: 'IN_PROGRESS', displayName: 'In Progress' },
      { code: 'WAITING_FOR_CUSTOMER', displayName: 'Waiting for Customer' },
      { code: 'RESOLVED', displayName: 'Resolved' },
      { code: 'CLOSED', displayName: 'Closed' }
    ];
    const idx = sequence.findIndex(s => s.code === current);
    if (idx >= 0 && idx < sequence.length - 1) {
      return sequence[idx + 1];
    }
    return null;
  }

  handleDragStart(event, ticketId) {
    this.draggedTicketId = ticketId;
    event.dataTransfer.setData('text/plain', ticketId);
  }

  handleDragOver(event) {
    event.preventDefault();
  }

  async handleDrop(event, targetStatus) {
    event.preventDefault();
    const ticketId = this.draggedTicketId || event.dataTransfer.getData('text/plain');
    if (!ticketId) return;

    await this.updateTicketStatus(ticketId, targetStatus);
    this.draggedTicketId = null;
  }

  async quickAdvanceStatus(ticketId, targetStatus) {
    await this.updateTicketStatus(ticketId, targetStatus);
  }

  async updateTicketStatus(ticketId, newStatus) {
    try {
      const res = await fetch(`${API_BASE}/tickets/${ticketId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus })
      });
      if (!res.ok) throw new Error('Status update failed');
      const updated = await res.json();
      this.showToast(`Ticket #${updated.ticketNumber} moved to ${updated.status}`, 'success');
      await this.refreshAllData();
    } catch (err) {
      this.showToast('Could not update status: ' + err.message, 'error');
    }
  }

  // -----------------------------------------------------------------
  // Ticket Table List & Filtering
  // -----------------------------------------------------------------
  async loadTicketsList() {
    const tableBody = document.getElementById('ticketsTableBody');
    if (!tableBody) return;

    const search = document.getElementById('searchInput')?.value.trim() || '';
    const status = document.getElementById('filterStatus')?.value || '';
    const priority = document.getElementById('filterPriority')?.value || '';
    const category = document.getElementById('filterCategory')?.value || '';
    const sortVal = document.getElementById('filterSort')?.value || 'createdAt-desc';

    const [sortBy, sortDir] = sortVal.split('-');

    const params = new URLSearchParams();
    if (search) params.append('search', search);
    if (status) params.append('status', status);
    if (priority) params.append('priority', priority);
    if (category) params.append('category', category);
    if (sortBy) params.append('sortBy', sortBy);
    if (sortDir) params.append('sortDir', sortDir);

    try {
      const res = await fetch(`${API_BASE}/tickets?${params.toString()}`);
      if (!res.ok) throw new Error('Failed to fetch ticket list');
      const tickets = await res.json();
      this.tickets = tickets;

      if (tickets.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="9" class="loading-cell">No tickets found matching current filters.</td></tr>`;
        return;
      }

      tableBody.innerHTML = tickets.map(t => {
        const dateStr = new Date(t.createdAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
        const prioClass = `priority-${t.priority.toLowerCase()}`;
        const statusBadgeClass = this.getStatusBadgeClass(t.status);

        return `
          <tr>
            <td>
              <a class="ticket-num-link" onclick="app.openTicketDetailModal(${t.id})">${this.escapeHtml(t.ticketNumber)}</a>
            </td>
            <td>
              <div style="font-weight:600;">${this.escapeHtml(t.customerName)}</div>
              <div style="font-size:0.75rem; color:#64748b;">${this.escapeHtml(t.customerEmail)}</div>
            </td>
            <td style="max-width:240px; font-weight:500;">${this.escapeHtml(t.subject)}</td>
            <td><span class="badge badge-subtle">${this.formatCategory(t.category)}</span></td>
            <td><span class="badge ${prioClass}">${t.priority}</span></td>
            <td><span class="badge ${statusBadgeClass}">${this.formatStatus(t.status)}</span></td>
            <td>${this.escapeHtml(t.assignedAgent || 'Unassigned')}</td>
            <td style="font-size:0.75rem; color:#64748b;">${dateStr}</td>
            <td>
              <div style="display:flex; gap:0.35rem;">
                <button class="btn btn-secondary btn-sm" onclick="app.openTicketDetailModal(${t.id})">View</button>
                <button class="btn btn-secondary btn-sm" style="color:#ef4444;" title="Delete Ticket" onclick="app.deleteTicket(${t.id})">✕</button>
              </div>
            </td>
          </tr>
        `;
      }).join('');
    } catch (err) {
      tableBody.innerHTML = `<tr><td colspan="9" class="loading-cell" style="color:#ef4444;">Error loading tickets: ${err.message}</td></tr>`;
    }
  }

  // -----------------------------------------------------------------
  // Create Ticket & Real-Time AI Auto-Classification
  // -----------------------------------------------------------------
  openNewTicketModal() {
    const form = document.getElementById('newTicketForm');
    if (form) form.reset();
    document.getElementById('aiClassificationBanner').style.display = 'none';
    this.openModal('newTicketModal');
  }

  async handleAutoClassify() {
    const subject = document.getElementById('ticketSubject').value.trim();
    const description = document.getElementById('ticketDescription').value.trim();
    const customerName = document.getElementById('ticketCustomerName').value.trim();

    if (!subject || !description) {
      this.showToast('Please enter both Subject and Description before running AI classification.', 'info');
      return;
    }

    const btn = document.getElementById('aiAutoClassifyBtn');
    btn.disabled = true;
    btn.innerHTML = 'Analyzing...';

    try {
      const res = await fetch(`${API_BASE}/ai/analyze`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ subject, description, customerName })
      });
      if (!res.ok) throw new Error('AI analysis failed');
      const data = await res.json();

      // Auto-select category and priority
      document.getElementById('ticketCategory').value = data.suggestedCategory;
      document.getElementById('ticketPriority').value = data.suggestedPriority;

      // Show banner
      const banner = document.getElementById('aiClassificationBanner');
      const confBadge = document.getElementById('aiConfidenceBadge');
      const reasoning = document.getElementById('aiReasoningText');

      confBadge.textContent = `${Math.round(data.confidenceScore * 100)}% Confidence (${data.mode === 'GEMINI_LIVE' ? 'Gemini Live' : 'Demo Engine'})`;
      reasoning.textContent = `${data.reasoning} Recommending Category: ${data.suggestedCategory} and Priority: ${data.suggestedPriority}.`;
      banner.style.display = 'block';

      this.showToast(`AI Auto-classified as ${data.suggestedCategory} (${data.suggestedPriority})`, 'success');
    } catch (err) {
      this.showToast('AI classification error: ' + err.message, 'error');
    } finally {
      btn.disabled = false;
      btn.innerHTML = `
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon></svg>
        AI Auto-Classify
      `;
    }
  }

  async handleCreateTicket(event) {
    event.preventDefault();
    const customerName = document.getElementById('ticketCustomerName').value.trim();
    const customerEmail = document.getElementById('ticketCustomerEmail').value.trim();
    const subject = document.getElementById('ticketSubject').value.trim();
    const description = document.getElementById('ticketDescription').value.trim();
    const category = document.getElementById('ticketCategory').value;
    const priority = document.getElementById('ticketPriority').value;

    const submitBtn = document.getElementById('submitTicketBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Submitting...';

    try {
      const res = await fetch(`${API_BASE}/tickets`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ customerName, customerEmail, subject, description, category, priority })
      });

      if (!res.ok) {
        const errorData = await res.json();
        throw new Error(errorData.message || 'Validation failed');
      }

      const created = await res.json();
      this.showToast(`Ticket #${created.ticketNumber} created successfully!`, 'success');
      this.closeModal('newTicketModal');
      await this.refreshAllData();
    } catch (err) {
      this.showToast('Failed to create ticket: ' + err.message, 'error');
    } finally {
      submitBtn.disabled = false;
      submitBtn.textContent = 'Create Ticket';
    }
  }

  // -----------------------------------------------------------------
  // Ticket Details, AI Reply Drafting & Resolution
  // -----------------------------------------------------------------
  async openTicketDetailModal(ticketId) {
    try {
      const res = await fetch(`${API_BASE}/tickets/${ticketId}`);
      if (!res.ok) throw new Error('Failed to load ticket details');
      const ticket = await res.json();
      this.currentTicket = ticket;

      document.getElementById('detailTicketNumber').textContent = ticket.ticketNumber;
      document.getElementById('detailSubject').textContent = ticket.subject;
      document.getElementById('detailCustomerName').textContent = ticket.customerName;
      document.getElementById('detailCustomerEmail').textContent = ticket.customerEmail;
      document.getElementById('detailCategory').textContent = this.formatCategory(ticket.category);
      document.getElementById('detailCreatedAt').textContent = new Date(ticket.createdAt).toLocaleString();
      document.getElementById('detailDescription').textContent = ticket.description;

      // Status & Priority Badges
      const statusBadge = document.getElementById('detailStatusBadge');
      statusBadge.className = `badge ${this.getStatusBadgeClass(ticket.status)}`;
      statusBadge.textContent = this.formatStatus(ticket.status);

      const prioBadge = document.getElementById('detailPriorityBadge');
      prioBadge.className = `badge priority-${ticket.priority.toLowerCase()}`;
      prioBadge.textContent = ticket.priority;

      // Controls form values
      document.getElementById('detailStatusSelect').value = ticket.status;
      document.getElementById('detailPrioritySelect').value = ticket.priority;
      document.getElementById('detailAgentSelect').value = ticket.assignedAgent || 'Unassigned';
      document.getElementById('detailResolutionNotes').value = ticket.resolutionNotes || '';

      // AI Draft section reset
      document.getElementById('aiDraftContainer').style.display = 'none';

      // Comments Thread
      this.renderComments(ticket.comments || []);

      this.openModal('ticketDetailModal');
    } catch (err) {
      this.showToast('Could not load ticket: ' + err.message, 'error');
    }
  }

  renderComments(comments) {
    const timeline = document.getElementById('commentsTimeline');
    if (!timeline) return;

    if (comments.length === 0) {
      timeline.innerHTML = '<div style="color:#94a3b8; font-size:0.8rem; text-align:center; padding:1rem 0;">No comments logged yet.</div>';
      return;
    }

    timeline.innerHTML = comments.map(c => {
      let roleClass = 'comment-agent';
      if (c.internalNote) roleClass = 'comment-internal';
      else if (c.authorRole === 'CUSTOMER') roleClass = 'comment-customer';
      else if (c.authorRole === 'SYSTEM') roleClass = 'comment-system';

      const timeStr = new Date(c.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', month: 'short', day: 'numeric' });

      return `
        <div class="comment-bubble ${roleClass}">
          <div class="comment-header">
            <span>${this.escapeHtml(c.authorName)} ${c.internalNote ? '<span style="color:#d97706; font-weight:700;">[Internal Note]</span>' : ''}</span>
            <span class="comment-time">${timeStr}</span>
          </div>
          <div>${this.escapeHtml(c.message)}</div>
        </div>
      `;
    }).join('');

    timeline.scrollTop = timeline.scrollHeight;
  }

  async generateAiResponseDraft() {
    if (!this.currentTicket) return;
    const btn = document.getElementById('generateAiDraftBtn');
    btn.disabled = true;
    btn.textContent = 'Generating...';

    try {
      const res = await fetch(`${API_BASE}/ai/analyze`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          subject: this.currentTicket.subject,
          description: this.currentTicket.description,
          customerName: this.currentTicket.customerName
        })
      });
      if (!res.ok) throw new Error('AI draft generation failed');
      const data = await res.json();

      document.getElementById('aiDraftText').textContent = data.draftedResponse;
      document.getElementById('aiDraftContainer').style.display = 'block';
      this.showToast('AI Draft Response ready!', 'success');
    } catch (err) {
      this.showToast('AI generation error: ' + err.message, 'error');
    } finally {
      btn.disabled = false;
      btn.textContent = 'Generate AI Reply';
    }
  }

  copyAiDraftToReply() {
    const draftText = document.getElementById('aiDraftText').textContent;
    const replyInput = document.getElementById('commentMessageInput');
    if (replyInput && draftText) {
      replyInput.value = draftText;
      replyInput.focus();
      this.showToast('AI Draft inserted into response box.', 'info');
    }
  }

  async handleAddComment(event) {
    event.preventDefault();
    if (!this.currentTicket) return;

    const message = document.getElementById('commentMessageInput').value.trim();
    const isInternalNote = document.getElementById('isInternalNoteCheckbox').checked;

    if (!message) return;

    try {
      const res = await fetch(`${API_BASE}/tickets/${this.currentTicket.id}/comments`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          authorName: isInternalNote ? 'Alex Rivera (Internal)' : 'Alex Rivera',
          authorRole: 'AGENT',
          message: message,
          internalNote: isInternalNote
        })
      });

      if (!res.ok) throw new Error('Failed to post comment');
      const comment = await res.json();

      document.getElementById('commentMessageInput').value = '';
      document.getElementById('isInternalNoteCheckbox').checked = false;

      // Append comment to local ticket thread
      this.currentTicket.comments.push(comment);
      this.renderComments(this.currentTicket.comments);
      this.showToast('Comment posted successfully', 'success');
    } catch (err) {
      this.showToast('Error posting comment: ' + err.message, 'error');
    }
  }

  async handleUpdateControls(event) {
    event.preventDefault();
    if (!this.currentTicket) return;

    const status = document.getElementById('detailStatusSelect').value;
    const priority = document.getElementById('detailPrioritySelect').value;
    const assignedAgent = document.getElementById('detailAgentSelect').value;
    const resolutionNotes = document.getElementById('detailResolutionNotes').value.trim();

    try {
      const res = await fetch(`${API_BASE}/tickets/${this.currentTicket.id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status, priority, assignedAgent, resolutionNotes })
      });

      if (!res.ok) throw new Error('Failed to update ticket');
      const updated = await res.json();
      this.currentTicket = updated;

      // Update badges
      const statusBadge = document.getElementById('detailStatusBadge');
      statusBadge.className = `badge ${this.getStatusBadgeClass(updated.status)}`;
      statusBadge.textContent = this.formatStatus(updated.status);

      const prioBadge = document.getElementById('detailPriorityBadge');
      prioBadge.className = `badge priority-${updated.priority.toLowerCase()}`;
      prioBadge.textContent = updated.priority;

      this.renderComments(updated.comments || []);
      this.showToast(`Ticket #${updated.ticketNumber} saved successfully`, 'success');
      await this.refreshAllData();
    } catch (err) {
      this.showToast('Error updating ticket: ' + err.message, 'error');
    }
  }

  async deleteTicket(ticketId) {
    if (!confirm('Are you sure you want to delete this ticket? This action cannot be undone.')) {
      return;
    }

    try {
      const res = await fetch(`${API_BASE}/tickets/${ticketId}`, { method: 'DELETE' });
      if (!res.ok) throw new Error('Failed to delete ticket');
      this.showToast('Ticket deleted successfully', 'info');
      await this.refreshAllData();
    } catch (err) {
      this.showToast('Delete error: ' + err.message, 'error');
    }
  }

  // -----------------------------------------------------------------
  // Modal & Helper Utilities
  // -----------------------------------------------------------------
  openModal(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'flex';
  }

  closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.style.display = 'none';
  }

  getStatusBadgeClass(status) {
    switch (status) {
      case 'NEW': return 'badge-new';
      case 'IN_PROGRESS': return 'badge-in-progress';
      case 'WAITING_FOR_CUSTOMER': return 'badge-waiting';
      case 'RESOLVED': return 'badge-resolved';
      case 'CLOSED': return 'badge-closed';
      default: return 'badge-subtle';
    }
  }

  formatStatus(status) {
    const map = {
      NEW: 'New',
      IN_PROGRESS: 'In Progress',
      WAITING_FOR_CUSTOMER: 'Waiting for Customer',
      RESOLVED: 'Resolved',
      CLOSED: 'Closed'
    };
    return map[status] || status;
  }

  formatCategory(category) {
    const map = {
      TECHNICAL: 'Technical Support',
      BILLING: 'Billing & Invoices',
      ACCOUNT_ACCESS: 'Account & Login',
      FEATURE_REQUEST: 'Feature Request',
      GENERAL_INQUIRY: 'General Inquiry'
    };
    return map[category] || category;
  }

  showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
      <span>${type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ'}</span>
      <span>${this.escapeHtml(message)}</span>
    `;

    container.appendChild(toast);
    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transition = 'opacity 0.3s ease';
      setTimeout(() => toast.remove(), 300);
    }, 3500);
  }

  escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }
}

// Instantiate App when DOM is loaded
let app;
document.addEventListener('DOMContentLoaded', () => {
  app = new TicketingApp();
});
