/* ==========================================================
   EquipTrack — Lógica do Cliente (Interface Monocromática)
   ========================================================== */

const API_BASE = window.location.origin;

const state = {
  activeTab: 'emprestimos',
  emprestimos: [],
  equipamentos: [],
  colaboradores: []
};

// ================= Inicialização =================
document.addEventListener('DOMContentLoaded', () => {
  setupTabs();
  loadAllData();

  const filterAtrasados = document.getElementById('filter-atrasados');
  if (filterAtrasados) {
    filterAtrasados.addEventListener('change', () => loadEmprestimos());
  }

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
      document.querySelectorAll('.modal-root.active').forEach(m => m.classList.remove('active'));
    }
  });
});

function setupTabs() {
  const tabs = document.querySelectorAll('.nav-tab');
  tabs.forEach(tab => {
    tab.addEventListener('click', () => {
      tabs.forEach(t => t.classList.remove('active'));
      tab.classList.add('active');

      const targetView = tab.dataset.tab;
      state.activeTab = targetView;

      document.querySelectorAll('.tab-view').forEach(view => {
        view.classList.remove('active');
      });
      document.getElementById(`view-${targetView}`).classList.add('active');
    });
  });
}

async function loadAllData() {
  await Promise.all([
    loadEmprestimos(),
    loadEquipamentos(),
    loadColaboradores()
  ]);
  updateKpis();
}

// ================= Requisições HTTP =================
async function apiRequest(endpoint, options = {}) {
  try {
    const res = await fetch(`${API_BASE}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {})
      },
      ...options
    });

    if (res.status === 204) return null;

    const data = await res.json().catch(() => null);

    if (!res.ok) {
      const errorMsg = data?.mensagem || `Erro HTTP ${res.status}`;
      throw new Error(errorMsg);
    }

    return data;
  } catch (err) {
    console.error(`Erro na rota [${endpoint}]:`, err);
    throw err;
  }
}

// ================= Atualização de Indicadores =================
function updateKpis() {
  const totalEquip = state.equipamentos.length;
  const availableEquip = state.equipamentos.filter(e => e.disponivel).length;
  const activeLoans = state.emprestimos.filter(l => !l.dataDevolucao).length;
  const delayedLoans = state.emprestimos.filter(l => {
    if (l.dataDevolucao) return false;
    return new Date(l.dataPrevistaDevolucao) < new Date(new Date().setHours(0, 0, 0, 0));
  }).length;

  document.getElementById('kpi-total-equip').textContent = totalEquip;
  document.getElementById('kpi-available-equip').textContent = availableEquip;
  document.getElementById('kpi-active-loans').textContent = activeLoans;
  document.getElementById('kpi-delayed-loans').textContent = delayedLoans;
}

// ================= EMPRÉSTIMOS =================
async function loadEmprestimos() {
  const tbody = document.getElementById('emprestimos-tbody');
  const onlyAtrasados = document.getElementById('filter-atrasados')?.checked;

  try {
    const url = onlyAtrasados ? '/emprestimos?atrasados=true' : '/emprestimos';
    const data = await apiRequest(url);
    state.emprestimos = data || [];
    renderEmprestimos(state.emprestimos);
    updateKpis();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Falha ao carregar empréstimos: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderEmprestimos(list) {
  const tbody = document.getElementById('emprestimos-tbody');
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state">Nenhum registro encontrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map(item => {
    const isDevolvido = !!item.dataDevolucao;
    const isAtrasado = !isDevolvido && (new Date(item.dataPrevistaDevolucao) < new Date(new Date().setHours(0, 0, 0, 0)));

    let statusTag = '';
    if (isDevolvido) {
      statusTag = `<span class="tag tag-muted">DEVOLVIDO (${formatDate(item.dataDevolucao)})</span>`;
    } else if (isAtrasado) {
      statusTag = `<span class="tag tag-alert">[ATRASADO]</span>`;
    } else {
      statusTag = `<span class="tag tag-default">EM ANDAMENTO</span>`;
    }

    const actionMarkup = !isDevolvido
      ? `<button class="btn btn-light btn-compact" onclick="devolverEmprestimo(${item.id})">Devolver</button>`
      : `<span style="color:var(--gray-400);font-family:var(--font-mono);font-size:11px;">Finalizado</span>`;

    return `
      <tr>
        <td><code>#${item.id}</code></td>
        <td>
          <div style="font-weight:600">${escapeHtml(item.equipamento?.nome || '—')}</div>
          <small style="color:var(--gray-600);font-family:var(--font-mono);font-size:11px;">Patr: ${escapeHtml(item.equipamento?.patrimonio || '—')}</small>
        </td>
        <td>
          <div>${escapeHtml(item.colaborador?.nome || '—')}</div>
          <small style="color:var(--gray-600);font-family:var(--font-mono);font-size:11px;">${escapeHtml(item.colaborador?.email || '')}</small>
        </td>
        <td style="font-family:var(--font-mono);font-size:12px;">${formatDate(item.dataEmprestimo)}</td>
        <td style="font-family:var(--font-mono);font-size:12px;">${formatDate(item.dataPrevistaDevolucao)}</td>
        <td>${statusTag}</td>
        <td class="col-action">${actionMarkup}</td>
      </tr>
    `;
  }).join('');
}

async function devolverEmprestimo(id) {
  if (!confirm(`Confirma a devolução do empréstimo #${id}?`)) return;

  try {
    await apiRequest(`/emprestimos/${id}/devolucao`, { method: 'PUT' });
    showToast(`Empréstimo #${id} devolvido com sucesso.`);
    await loadAllData();
  } catch (err) {
    showToast(err.message, 'danger');
  }
}

async function handleSalvarEmprestimo(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-submit-emprestimo');
  btn.disabled = true;

  const payload = {
    equipamentoId: Number(document.getElementById('select-equipamento').value),
    colaboradorId: Number(document.getElementById('select-colaborador').value)
  };

  try {
    await apiRequest('/emprestimos', {
      method: 'POST',
      body: JSON.stringify(payload)
    });
    showToast('Empréstimo registrado com sucesso.');
    closeModal('modal-emprestimo');
    await loadAllData();
  } catch (err) {
    showToast(err.message, 'danger');
  } finally {
    btn.disabled = false;
  }
}

// ================= EQUIPAMENTOS =================
async function loadEquipamentos() {
  const tbody = document.getElementById('equipamentos-tbody');
  const tipo = document.getElementById('filter-equip-tipo')?.value || '';
  const disponivel = document.getElementById('filter-equip-disp')?.value || '';

  try {
    let queryParams = [];
    if (tipo) queryParams.push(`tipo=${tipo}`);
    if (disponivel) queryParams.push(`disponivel=${disponivel}`);
    const query = queryParams.length ? `?${queryParams.join('&')}` : '';

    const data = await apiRequest(`/equipamentos${query}`);
    state.equipamentos = data || [];
    renderEquipamentos(state.equipamentos);
    populateSelectEquipamentos();
    updateKpis();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Falha ao carregar equipamentos: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderEquipamentos(list) {
  const tbody = document.getElementById('equipamentos-tbody');
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Nenhum equipamento cadastrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map(item => {
    const situacaoTag = item.disponivel
      ? `<span class="tag tag-default">DISPONÍVEL</span>`
      : `<span class="tag tag-solid">EMPRESTADO</span>`;

    return `
      <tr>
        <td><code>${escapeHtml(item.patrimonio)}</code></td>
        <td style="font-weight:600">${escapeHtml(item.nome)}</td>
        <td><span class="tag tag-default">${escapeHtml(item.tipo)}</span></td>
        <td>${situacaoTag}</td>
        <td class="col-action">
          <button class="btn-link" onclick="editarEquipamento(${item.id})">Editar</button>
          <button class="btn-link btn-link-danger" style="margin-left:10px;" onclick="excluirEquipamento(${item.id})">Excluir</button>
        </td>
      </tr>
    `;
  }).join('');
}

function populateSelectEquipamentos() {
  const select = document.getElementById('select-equipamento');
  if (!select) return;

  const disponiveis = state.equipamentos.filter(e => e.disponivel);
  select.innerHTML = '<option value="">Selecione...</option>' +
    disponiveis.map(e => `<option value="${e.id}">${escapeHtml(e.patrimonio)} — ${escapeHtml(e.nome)} (${e.tipo})</option>`).join('');
}

async function handleSalvarEquipamento(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-submit-equipamento');
  btn.disabled = true;

  const id = document.getElementById('equipamento-id').value;
  const payload = {
    patrimonio: document.getElementById('equipamento-patrimonio').value.trim(),
    nome: document.getElementById('equipamento-nome').value.trim(),
    tipo: document.getElementById('equipamento-tipo').value
  };

  try {
    if (id) {
      await apiRequest(`/equipamentos/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      showToast('Equipamento atualizado.');
    } else {
      await apiRequest('/equipamentos', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      showToast('Equipamento cadastrado.');
    }
    closeModal('modal-equipamento');
    await loadEquipamentos();
  } catch (err) {
    showToast(err.message, 'danger');
  } finally {
    btn.disabled = false;
  }
}

function editarEquipamento(id) {
  const equip = state.equipamentos.find(e => e.id === id);
  if (!equip) return;

  document.getElementById('equipamento-id').value = equip.id;
  document.getElementById('equipamento-patrimonio').value = equip.patrimonio;
  document.getElementById('equipamento-patrimonio').disabled = true;
  document.getElementById('equipamento-nome').value = equip.nome;
  document.getElementById('equipamento-tipo').value = equip.tipo;

  document.getElementById('modal-equipamento-title').textContent = 'Editar Equipamento';
  openModal('modal-equipamento');
}

async function excluirEquipamento(id) {
  if (!confirm('Confirma a exclusão deste equipamento?')) return;

  try {
    await apiRequest(`/equipamentos/${id}`, { method: 'DELETE' });
    showToast('Equipamento excluído.');
    await loadEquipamentos();
  } catch (err) {
    showToast(err.message, 'danger');
  }
}

// ================= COLABORADORES =================
async function loadColaboradores() {
  const tbody = document.getElementById('colaboradores-tbody');

  try {
    const data = await apiRequest('/colaboradores');
    state.colaboradores = data || [];
    renderColaboradores(state.colaboradores);
    populateSelectColaboradores();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Falha ao carregar colaboradores: ${escapeHtml(err.message)}</td></tr>`;
  }
}

function renderColaboradores(list) {
  const tbody = document.getElementById('colaboradores-tbody');
  if (!list.length) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state">Nenhum colaborador cadastrado.</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map(item => {
    const endereco = [item.logradouro, item.numero, item.bairro, item.cidade, item.uf]
      .filter(Boolean)
      .join(', ');

    return `
      <tr>
        <td style="font-weight:600">${escapeHtml(item.nome)}</td>
        <td><code>${escapeHtml(item.email)}</code></td>
        <td><code style="font-size:11px;">${escapeHtml(item.cep)}</code></td>
        <td style="color:var(--gray-700);">${escapeHtml(endereco || '—')}</td>
        <td class="col-action">
          <button class="btn btn-light btn-compact" onclick="verHistoricoColaborador(${item.id}, '${escapeHtml(item.nome)}')">Histórico</button>
          <button class="btn-link" style="margin-left:8px;" onclick="editarColaborador(${item.id})">Editar</button>
          <button class="btn-link btn-link-danger" style="margin-left:8px;" onclick="excluirColaborador(${item.id})">Excluir</button>
        </td>
      </tr>
    `;
  }).join('');
}

function populateSelectColaboradores() {
  const select = document.getElementById('select-colaborador');
  if (!select) return;

  select.innerHTML = '<option value="">Selecione...</option>' +
    state.colaboradores.map(c => `<option value="${c.id}">${escapeHtml(c.nome)} (${escapeHtml(c.email)})</option>`).join('');
}

async function handleSalvarColaborador(e) {
  e.preventDefault();
  const btn = document.getElementById('btn-submit-colaborador');
  btn.disabled = true;

  const id = document.getElementById('colaborador-id').value;
  const cepDigits = document.getElementById('colaborador-cep').value.replace(/\D/g, '');

  const payload = {
    nome: document.getElementById('colaborador-nome').value.trim(),
    email: document.getElementById('colaborador-email').value.trim(),
    cep: cepDigits,
    numero: document.getElementById('colaborador-numero').value.trim(),
    logradouro: document.getElementById('colaborador-logradouro').value.trim(),
    bairro: document.getElementById('colaborador-bairro').value.trim(),
    cidade: document.getElementById('colaborador-cidade').value.trim(),
    uf: document.getElementById('colaborador-uf').value.trim().toUpperCase()
  };

  try {
    if (id) {
      await apiRequest(`/colaboradores/${id}`, {
        method: 'PUT',
        body: JSON.stringify(payload)
      });
      showToast('Colaborador atualizado.');
    } else {
      await apiRequest('/colaboradores', {
        method: 'POST',
        body: JSON.stringify(payload)
      });
      showToast('Colaborador cadastrado.');
    }
    closeModal('modal-colaborador');
    await loadColaboradores();
  } catch (err) {
    showToast(err.message, 'danger');
  } finally {
    btn.disabled = false;
  }
}

function editarColaborador(id) {
  const c = state.colaboradores.find(item => item.id === id);
  if (!c) return;

  document.getElementById('colaborador-id').value = c.id;
  document.getElementById('colaborador-nome').value = c.nome;
  document.getElementById('colaborador-email').value = c.email;
  document.getElementById('colaborador-cep').value = c.cep;
  document.getElementById('colaborador-numero').value = c.numero;
  document.getElementById('colaborador-logradouro').value = c.logradouro || '';
  document.getElementById('colaborador-bairro').value = c.bairro || '';
  document.getElementById('colaborador-cidade').value = c.cidade || '';
  document.getElementById('colaborador-uf').value = c.uf || '';

  document.getElementById('modal-colaborador-title').textContent = 'Editar Colaborador';
  openModal('modal-colaborador');
}

async function excluirColaborador(id) {
  if (!confirm('Confirma a exclusão deste colaborador?')) return;

  try {
    await apiRequest(`/colaboradores/${id}`, { method: 'DELETE' });
    showToast('Colaborador excluído.');
    await loadColaboradores();
  } catch (err) {
    showToast(err.message, 'danger');
  }
}

async function verHistoricoColaborador(id, nome) {
  document.getElementById('historico-colaborador-nome').textContent = `Colaborador: ${nome}`;
  const tbody = document.getElementById('historico-tbody');
  tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Carregando histórico...</td></tr>`;
  openModal('modal-historico');

  try {
    const data = await apiRequest(`/colaboradores/${id}/emprestimos`);
    if (!data || !data.length) {
      tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Nenhum empréstimo registrado.</td></tr>`;
      return;
    }

    tbody.innerHTML = data.map(item => {
      const isDevolvido = !!item.dataDevolucao;
      const isAtrasado = !isDevolvido && (new Date(item.dataPrevistaDevolucao) < new Date(new Date().setHours(0, 0, 0, 0)));

      let statusTag = '';
      if (isDevolvido) {
        statusTag = `<span class="tag tag-muted">Devolvido</span>`;
      } else if (isAtrasado) {
        statusTag = `<span class="tag tag-alert">[Atrasado]</span>`;
      } else {
        statusTag = `<span class="tag tag-default">Em andamento</span>`;
      }

      return `
        <tr>
          <td><code>#${item.id}</code></td>
          <td>${escapeHtml(item.equipamento?.nome || '—')} <small style="color:var(--gray-600);font-family:var(--font-mono);">(${escapeHtml(item.equipamento?.patrimonio || '')})</small></td>
          <td style="font-family:var(--font-mono);font-size:12px;">${formatDate(item.dataEmprestimo)}</td>
          <td style="font-family:var(--font-mono);font-size:12px;">${formatDate(item.dataPrevistaDevolucao)}</td>
          <td style="font-family:var(--font-mono);font-size:12px;">${item.dataDevolucao ? formatDate(item.dataDevolucao) : '—'}</td>
          <td>${statusTag}</td>
        </tr>
      `;
    }).join('');
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="6" class="empty-state">Erro: ${escapeHtml(err.message)}</td></tr>`;
  }
}

async function buscarCep() {
  const cepInput = document.getElementById('colaborador-cep');
  const cep = cepInput.value.replace(/\D/g, '');

  if (cep.length !== 8) {
    showToast('Informe um CEP com 8 dígitos.', 'warning');
    return;
  }

  try {
    const res = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
    const data = await res.json();
    if (data.erro) {
      showToast('CEP não encontrado.', 'warning');
      return;
    }

    document.getElementById('colaborador-logradouro').value = data.logradouro || '';
    document.getElementById('colaborador-bairro').value = data.bairro || '';
    document.getElementById('colaborador-cidade').value = data.localidade || '';
    document.getElementById('colaborador-uf').value = data.uf || '';
    document.getElementById('colaborador-numero').focus();
    showToast('Endereço preenchido via CEP.');
  } catch {
    showToast('Não foi possível consultar o ViaCEP. Preencha manualmente.', 'warning');
  }
}

// ================= Modais =================
function openModal(id) {
  const modal = document.getElementById(id);
  if (!modal) return;

  if (id === 'modal-equipamento' && !document.getElementById('equipamento-id').value) {
    document.getElementById('form-equipamento').reset();
    document.getElementById('modal-equipamento-title').textContent = 'Novo Equipamento';
    document.getElementById('equipamento-patrimonio').disabled = false;
  }
  if (id === 'modal-colaborador' && !document.getElementById('colaborador-id').value) {
    document.getElementById('form-colaborador').reset();
    document.getElementById('modal-colaborador-title').textContent = 'Novo Colaborador';
  }
  if (id === 'modal-emprestimo') {
    document.getElementById('form-emprestimo').reset();
    populateSelectEquipamentos();
    populateSelectColaboradores();
  }

  modal.classList.add('active');
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) {
    modal.classList.remove('active');
    if (id === 'modal-equipamento') document.getElementById('equipamento-id').value = '';
    if (id === 'modal-colaborador') document.getElementById('colaborador-id').value = '';
  }
}

// ================= Notificações Minimalistas =================
function showToast(message, type = 'info') {
  const container = document.getElementById('toast-container');
  if (!container) return;

  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transition = 'opacity 0.2s ease';
    setTimeout(() => toast.remove(), 200);
  }, 3500);
}

// ================= Auxiliares =================
function formatDate(dateStr) {
  if (!dateStr) return '—';
  const [year, month, day] = dateStr.split('-');
  return `${day}/${month}/${year}`;
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}
